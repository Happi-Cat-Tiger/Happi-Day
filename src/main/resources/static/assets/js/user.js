const getJwt = () => localStorage.getItem('token')

const fetchLoggedIn = function () {
    if (!getJwt())
        return new Promise(resolve => {
            resolve(false)
        })
    else return fetch("/api/v1/user/info", {
        headers: {
            'Authorization': `Bearer ${getJwt()}`
        }
    }).then(response => {
        if (response.ok) return response.json()
        else return false
    })
}

const createUserElem = function () {
    return fetchLoggedIn().then(userInfo => {
        if (!userInfo) return anonymousUserElem()
        else return loggedInUserElem(userInfo.username)
    })
}

const loggedInUserElem = function (username) {
    const container = document.createElement('div')
    const usernameElem = document.createElement('div')
    usernameElem.innerText = `${username}님, 반갑습니다! `

    const logoutElem = document.createElement('form')
    const logoutButton = document.createElement('input')
    logoutButton.value = '로그아웃'
    logoutButton.type = 'submit'
    logoutElem.appendChild(logoutButton)
    logoutElem.addEventListener('submit', e => {
        e.preventDefault()
        localStorage.removeItem('token')
        location.reload()
    })

    const chatLinkElem = document.createElement('a')
    chatLinkElem.innerText = '채팅하기'
    chatLinkElem.href = '/views/chat'

    container.appendChild(usernameElem)
    container.appendChild(logoutElem)
    container.appendChild(chatLinkElem)

    return container
}

const anonymousUserElem = function () {
    const container = document.createElement('div')
    const loginLink = document.createElement('a')
    loginLink.href = `/views/login`
    loginLink.innerText = '로그인'

    const nbsp = document.createTextNode(' ')
    const registerLink = document.createElement('a')
    registerLink.href = `/views/register`
    registerLink.innerText = '회원가입'

    container.appendChild(loginLink)
    container.appendChild(nbsp)
    container.appendChild(registerLink)

    return container
}

const requireLogin = function() {
    alert('로그인이 필요합니다.')
    location.href = `/views/login`
}
