package com.happiday.Happi_Day.initialization;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class DbSeeder implements CommandLineRunner {

    private final DataInitManager initManager;

    @Override
    public void run(String... args) throws Exception {
        initManager.init();
    }
}
