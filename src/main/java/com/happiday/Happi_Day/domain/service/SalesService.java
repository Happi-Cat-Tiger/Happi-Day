package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSales;
import com.happiday.Happi_Day.domain.entity.product.*;
import com.happiday.Happi_Day.domain.entity.product.dto.*;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamSales;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.utils.FileUtils;
import com.happiday.Happi_Day.utils.HashtagUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SalesService {
    private final UserRepository userRepository;
    private final SalesCategoryRepository salesCategoryRepository;
    private final SalesRepository salesRepository;
    private final ProductRepository productRepository;
    private final ArtistRepository artistRepository;
    private final ArtistSalesRepository artistSalesRepository;
    private final TeamRepository teamRepository;
    private final TeamSalesRepository teamSalesRepository;
    private final HashtagRepository hashtagRepository;
    private final FileUtils fileUtils;
    private final QuerySalesRepository querySalesRepository;
    private final RedisService redisService;
    private final SalesLikeRepository salesLikeRepository;
    private final SalesHashtagRepository salesHashtagRepository;
    private final ProductService productService;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryService deliveryService;

    @Transactional
    public ReadOneSalesDto createSales(Long categoryId, WriteSalesDto salesDto, List<CreateProductDto> productDtos, List<CreateDeliveryDto> deliveryDtos, MultipartFile thumbnailImage, List<MultipartFile> imageFile, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 판매글 카테고리
        SalesCategory category = salesCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        if (productDtos.isEmpty()) {
            throw new CustomException(ErrorCode.PRODUCT_IS_EMPTY);
        }
        List<Product> productList = new ArrayList<>();

        if (deliveryDtos.isEmpty()) {
            throw new CustomException(ErrorCode.DELIVERY_IS_EMPTY);
        }

        if (salesDto.getEndTime().isBefore(salesDto.getStartTime()))
            throw new CustomException(ErrorCode.END_TIME_ERROR);

        HashtagUtils hashtagUtils = new HashtagUtils(artistRepository, teamRepository, hashtagRepository);
        Triple<List<Artist>, List<Team>, List<Hashtag>> processedTags = hashtagUtils.processTags(salesDto.getHashtag());

        Sales newSales = Sales.builder()
                .users(user)
                .salesStatus(SalesStatus.ON_SALE)
                .salesCategory(category)
                .name(salesDto.getName())
                .namePrice(salesDto.getNamePrice())
                .artistSalesList(new ArrayList<>())
                .teamSalesList(new ArrayList<>())
                .salesHashtags(new ArrayList<>())
                .description(salesDto.getDescription())
                .products(productList)
                .salesLikes(new ArrayList<>())
                .imageUrl(new ArrayList<>())
                .accountName(salesDto.getAccountName())
                .accountNumber(salesDto.getAccountNumber())
                .accountUser(salesDto.getAccountUser())
                .startTime(salesDto.getStartTime())
                .endTime(salesDto.getEndTime())
                .build();

        // 아티스트와 판매글의 관계 설정
        List<Artist> artists = processedTags.getLeft();
        for (Artist artist : artists) {
            ArtistSales artistSales = ArtistSales.builder()
                    .sales(newSales)
                    .artist(artist)
                    .build();
            artistSalesRepository.save(artistSales);
            newSales.getArtistSalesList().add(artistSales);
        }

        // 팀과 판매글의 관계 설정
        List<Team> teams = processedTags.getMiddle();
        for (Team team : teams) {
            TeamSales teamSales = TeamSales.builder()
                    .sales(newSales)
                    .team(team)
                    .build();
            teamSalesRepository.save(teamSales);
            newSales.getTeamSalesList().add(teamSales);
        }

        List<SalesHashtag> salesHashtags = new ArrayList<>();
        List<Hashtag> hashtags = processedTags.getRight();

        for (Hashtag hashtag : hashtags) {
            SalesHashtag salesHashtag = SalesHashtag.builder()
                    .sales(newSales)
                    .hashtag(hashtag)
                    .build();
            salesHashtags.add(salesHashtag);
            salesHashtagRepository.save(salesHashtag);
        }
        newSales.setSalesHashtags(salesHashtags);

        // 이미지 저장
        if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
            String saveThumbnailImage = fileUtils.uploadFile(thumbnailImage);
            newSales.setThumbnailImage(saveThumbnailImage);
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            List<String> imageList = new ArrayList<>();
            for (MultipartFile image : imageFile) {
                String imageUrl = fileUtils.uploadFile(image);
                imageList.add(imageUrl);
            }
            newSales.setImageUrl(imageList);
        }

        salesRepository.save(newSales);

        // 상품 옵션 저장
        for (CreateProductDto productDto : productDtos) {
            productService.createProduct(newSales.getId(), productDto, user.getUsername());
        }

        // 배송방법 저장
        for (CreateDeliveryDto deliveryDto : deliveryDtos) {
            deliveryService.createDelivery(newSales.getId(), deliveryDto, user.getUsername());
        }

        salesRepository.save(newSales);

        ReadOneSalesDto.fromEntity(newSales, new ArrayList<>());
        return ReadOneSalesDto.fromEntity(newSales, new ArrayList<>());
    }

    public Page<ReadListSalesDto> readOngoingSales(Long categoryId, Pageable pageable, String filter, String keyword) {
        log.info(filter);
        log.info(keyword);
        SalesCategory category = salesCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Page<Sales> salesList = querySalesRepository.findSalesByFilterAndKeywordOngoing(pageable, categoryId, filter, keyword);

        return salesList.map(ReadListSalesDto::fromEntity);
    }

    // 판매글 목록 조회
    public Page<ReadListSalesDto> readSalesList(Long categoryId, Pageable pageable, String filter, String keyword) {
        SalesCategory category = salesCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Page<Sales> salesList = querySalesRepository.findSalesByFilterAndKeyword(pageable, categoryId, filter, keyword);
        return salesList.map(ReadListSalesDto::fromEntity);
    }

    // 구독중인 아티스트의 굿즈/공구 리스트 조회
    public Page<ReadListSalesDto> readSalesBySubscribedArtists(Pageable pageable, Long categoryId, String filter, String keyword, String username) {
        SalesCategory category = salesCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<Sales> salesList = querySalesRepository.findSalesByFilterAndKeywordAndSubscribedArtists(pageable, categoryId, filter, keyword, user);

        return salesList.map(ReadListSalesDto::fromEntity);
    }

    // 구독, 진행중인 굿즈/공구 리스트 조회
    public Page<ReadListSalesDto> readOngoingSalesBySubscribedArtists(Pageable pageable, Long categoryId, String filter, String keyword, String username) {
        SalesCategory category = salesCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<Sales> saleList = querySalesRepository.findSalesByFilterAndKeywordAndOngoingAndSubscribedArtists(pageable, categoryId, filter, keyword, user);

        return saleList.map(ReadListSalesDto::fromEntity);
    }

    @Transactional
    public ReadOneSalesDto readSalesOne(String clientAddress, Long categoryId, Long salesId) {
        SalesCategory category = salesCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Sales sales = salesRepository.findById(salesId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALES_NOT_FOUND));

        if (redisService.isFirstIpRequest(clientAddress, salesId)) {
            log.debug("same user requests duplicate in 24hours: {}, {}", clientAddress, salesId);
            increaseViewCount(clientAddress, salesId);
        }

        List<ReadProductDto> dtoList = new ArrayList<>();
        for (Product product : sales.getProducts()) {
            dtoList.add(ReadProductDto.fromEntity(product));
        }

        return ReadOneSalesDto.fromEntity(sales, dtoList);
    }

    @Transactional
    public ReadOneSalesDto updateSales(Long salesId, UpdateSalesDto salesDto, List<CreateProductDto> productDtos, List<CreateDeliveryDto> deliveryDtos, MultipartFile thumbnailImage, List<MultipartFile> imageFile, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Sales sales = salesRepository.findById(salesId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALES_NOT_FOUND));

        // user 확인
        if (!user.equals(sales.getUsers())) throw new CustomException(ErrorCode.FORBIDDEN);

        if (salesDto.getEndTime().isBefore(salesDto.getStartTime()))
            throw new CustomException(ErrorCode.END_TIME_ERROR);

        HashtagUtils hashtagUtils = new HashtagUtils(artistRepository, teamRepository, hashtagRepository);
        Triple<List<Artist>, List<Team>, List<Hashtag>> processedTags = hashtagUtils.processTags(salesDto.getHashtag());

        if (processedTags.getRight() != null) {
            salesHashtagRepository.deleteBySales(sales);
            sales.getSalesHashtags().clear();

            List<Hashtag> hashtags = processedTags.getRight();
            for (Hashtag hashtag : hashtags) {
                SalesHashtag salesHashtag = SalesHashtag.builder()
                        .sales(sales)
                        .hashtag(hashtag)
                        .build();
                salesHashtagRepository.save(salesHashtag);
                sales.getSalesHashtags().add(salesHashtag);
            }
        }

        sales.updateSales(Sales.builder()
                .users(user)
                .name(salesDto.getName())
                .description(salesDto.getDescription())
                .accountNumber(salesDto.getAccountNumber())
                .accountName(salesDto.getAccountName())
                .accountUser(salesDto.getAccountUser())
                .startTime(salesDto.getStartTime())
                .endTime(salesDto.getEndTime())
                .namePrice(salesDto.getNamePrice())
                .build());

        // 아티스트와 판매글의 관계 설정
        List<Artist> artists = processedTags.getLeft();
        artistSalesRepository.deleteBySales(sales);
        sales.getArtistSalesList().clear();
        for (Artist artist : artists) {
            ArtistSales artistSales = ArtistSales.builder()
                    .sales(sales)
                    .artist(artist)
                    .build();
            artistSalesRepository.save(artistSales);
            sales.getArtistSalesList().add(artistSales);
        }

        // 팀과 판매글의 관계 설정
        List<Team> teams = processedTags.getMiddle();
        teamSalesRepository.deleteBySales(sales);
        sales.getTeamSalesList().clear();
        for (Team team : teams) {
            TeamSales teamSales = TeamSales.builder()
                    .sales(sales)
                    .team(team)
                    .build();
            teamSalesRepository.save(teamSales);
            sales.getTeamSalesList().add(teamSales);
        }

        if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
            if (sales.getThumbnailImage() != null && !sales.getThumbnailImage().isEmpty()) {
                try {
                    fileUtils.deleteFile(sales.getThumbnailImage());
                    log.info("썸네일 이미지 삭제완료");
                } catch (Exception e) {
                    log.error("썸네일 삭제 실패");
                }
            }
            String thumbnailImageUrl = fileUtils.uploadFile(thumbnailImage);
            sales.setThumbnailImage(thumbnailImageUrl);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            if (sales.getImageUrl() != null && !sales.getImageUrl().isEmpty()) {
                try {
                    for (String url : sales.getImageUrl()) {
                        fileUtils.deleteFile(url);
                        log.info("판매글 이미지 삭제완료");
                    }
                } catch (Exception e) {
                    log.error("판매글 이미지 삭제 실패");
                }
            }
            List<String> imageList = new ArrayList<>();
            for (MultipartFile image : imageFile) {
                String imageUrl = fileUtils.uploadFile(image);
                imageList.add(imageUrl);
            }
            sales.setImageUrl(imageList);
        }


        if (salesDto.getStatus() != null) {
            switch (salesDto.getStatus()) {
                case "판매중":
                    sales.updateStatus(SalesStatus.ON_SALE);
                    break;
                case "판매중지":
                    sales.updateStatus(SalesStatus.STOP_SALE);
                    break;
                case "품절":
                    sales.updateStatus(SalesStatus.OUT_OF_STOCK);
                    break;
                default:
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }

        salesRepository.save(sales);

        // 상품 옵션 저장
        for (CreateProductDto productDto : productDtos) {
            Optional<Product> product = productRepository.findByNameAndSales(productDto.getName(), sales);
            if (product.isPresent()) {
                productService.updateProduct(salesId, product.get().getId(), productDto, username);
            } else {
                productService.createProduct(salesId, productDto, username);
            }
        }

        // 배송방법 저장
        deliveryRepository.deleteAllBySales(sales);

        for (CreateDeliveryDto deliveryDto : deliveryDtos) {
            deliveryService.createDelivery(salesId, deliveryDto, username);
        }

        salesRepository.save(sales);

        List<ReadProductDto> dtoList = new ArrayList<>();
        for (Product product : sales.getProducts()) {
            dtoList.add(ReadProductDto.fromEntity(product));
        }
        return ReadOneSalesDto.fromEntity(sales, dtoList);
    }

    // 판매글 상태변경
//    @Transactional
//    public void updateStatus(Long salesId, String username, String status) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
//
//        Sales sales = salesRepository.findById(salesId)
//                .orElseThrow(() -> new CustomException(ErrorCode.SALES_NOT_FOUND));
//
//        if (!user.equals(sales.getUsers())) throw new CustomException(ErrorCode.FORBIDDEN);
//    }

    @Transactional
    public void deleteSales(Long categoryId, Long salesId, String username) {
        SalesCategory category = salesCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Sales sales = salesRepository.findById(salesId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALES_NOT_FOUND));

        if (!user.equals(sales.getUsers())) throw new CustomException(ErrorCode.FORBIDDEN);

        // 이미지 삭제
        if (sales.getImageUrl() != null) {
            for (String imageUrl : sales.getImageUrl()) {
                fileUtils.deleteFile(imageUrl);
            }
        }
        if (sales.getThumbnailImage() != null) {
            fileUtils.deleteFile(sales.getThumbnailImage());
        }

        productRepository.deleteAllBySales(sales);
        salesRepository.deleteById(salesId);
    }

    @Transactional
    public String likeSales(Long salesId, String username) {
        Sales sales = salesRepository.findById(salesId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALES_NOT_FOUND));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String resposne = "";
        Optional<SalesLike> salesLike = salesLikeRepository.findByUserAndSales(user, sales);

        if (salesLike.isPresent()) {
            salesLikeRepository.delete(salesLike.get());
            List<SalesLike> salesLikeUser = salesLikeRepository.findBySales(sales);
            resposne = "찜하기가 취소되었습니다. 현재 찜하기 수 : " + salesLikeUser.size();
        } else {
            SalesLike newSalesLike = SalesLike.builder()
                    .sales(sales)
                    .user(user)
                    .build();
            salesLikeRepository.save(newSalesLike);
            List<SalesLike> salesLikeUser = salesLikeRepository.findBySales(sales);
            resposne = "찜하기를 눌렀습니다. 현재 찜하기 수 : " + salesLikeUser.size();
        }
        return resposne;
    }

    @Transactional
    public void increaseViewCount(String clientAddress, Long salesId) {
        salesRepository.increaseViewCount(salesId);
        redisService.clientRequest(clientAddress, salesId);
    }
}
