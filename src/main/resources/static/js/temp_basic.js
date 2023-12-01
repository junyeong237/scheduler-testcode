const host = 'http://' + window.location.host;
let targetId;

$(document).ready(function () {
    const auth = getToken();
    if (auth !== undefined && auth !== '') {
        $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
            jqXHR.setRequestHeader('Authorization', auth);
            // ajax 요청헤더에 set
        });
    } else {
        window.location.href = host + '/user/login-page';
        return; //지워도 될듯하다
    }

    $.ajax({
        type: 'GET',
        url: `/user-info`,
        contentType: 'application/json',
    })
        .done(function (res, status, xhr) {
            alert('user-info 진입');
            const username = res.username;
            const isAdmin = !!res.admin;

            if (!username) {
                console.log('host:',host);
                window.location.href = '/user/login-page';
                return;
            }
            console.log(username)
            $('#username').text(username);
            if (isAdmin) {
                $('#admin').text(true);
                //showProduct(true);
            } else {
                alert('showproduct진입전')
                showProduct();
            }

        //     const cardsData = [
        //         { title: '할 일 1', description: '이것을 마무리하세요.' },
        //         { title: '할 일 2', description: '저것을 시작하세요.' },
        //         // 추가적인 카드 데이터는 이곳에 추가
        //     ];
        //
        //     const cardContainer = $('.card-container');
        //
        //     // 카드를 동적으로 생성하여 추가하는 함수
        //     function addCard(title, description) {
        //         const cardHtml = `
        //     <div class="card">
        //         <h3>${title}</h3>
        //         <p>${description}</p>
        //     </div>
        // `;
        //         cardContainer.append(cardHtml);
        //     }
        //
        //     // 예제 데이터를 이용하여 카드 생성
        //     cardsData.forEach(card => {
        //         addCard(card.title, card.description);
        //     });
        })
        .fail(function (jqXHR, textStatus) {
            logout();
        });

});


function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function execSearch() {
    /**
     * 검색어 input id: query
     * 검색결과 목록: #search-result-box
     * 검색결과 HTML 만드는 함수: addHTML
     */
        // 1. 검색창의 입력값을 가져온다.
    let query = $('#query').val();

    // 2. 검색창 입력값을 검사하고, 입력하지 않았을 경우 focus.
    if (query == '') {
        alert('검색어를 입력해주세요');
        $('#query').focus();
        return;
    }
    // 3. GET /api/search?query=${query} 요청
    $.ajax({
        type: 'GET',
        url: `/search?query=${query}`,
        success: function (response) {
            $('#search-result-box').empty();
            // 4. for 문마다 itemDto를 꺼내서 HTML 만들고 검색결과 목록에 붙이기!
            for (let i = 0; i < response.length; i++) {
                let itemDto = response[i];
                let tempHtml = addHTML(itemDto);
                $('#search-result-box').append(tempHtml);
            }
        },
        error(error, status, request) {
            logout();
        }
    })

}

function addHTML(itemDto) {
    /**
     * class="search-itemDto" 인 녀석에서
     * image, title, lprice, addProduct 활용하기
     * 참고) onclick='addProduct(${JSON.stringify(itemDto)})'
     */
    return `<div class="search-itemDto">
        <div class="search-itemDto-left">
            <img src="${itemDto.image}" alt="">
        </div>
        <div class="search-itemDto-center">
            <div>${itemDto.title}</div>
            <div class="price">
                ${numberWithCommas(itemDto.lprice)}
                <span class="unit">원</span>
            </div>
        </div>
        <div class="search-itemDto-right">
            <img src="../images/icon-save.png" alt="" onclick='addProduct(${JSON.stringify(itemDto)})'>
        </div>
    </div>`
}

function addProduct(itemDto) {
    /**
     * modal 뜨게 하는 법: $('#container').addClass('active');
     * data를 ajax로 전달할 때는 두 가지가 매우 중요
     * 1. contentType: "application/json",
     * 2. data: JSON.stringify(itemDto),
     */

    // 1. POST /api/products 에 관심 상품 생성 요청
    $.ajax({
        type: 'POST',
        url: '/products',
        contentType: 'application/json',
        data: JSON.stringify(itemDto),
        success: function (response) {
            // 2. 응답 함수에서 modal을 뜨게 하고, targetId 를 reponse.id 로 설정
            $('#container').addClass('active');
            targetId = response.id;
        },
        error(error, status, request) {
            logout();
        }
    });
}

function showProduct(isAdmin = false) {
    /**
     * 관심상품 목록: #product-container
     * 검색결과 목록: #search-result-box
     * 관심상품 HTML 만드는 함수: addProductItem
     */
    let dataSource = null;

    // admin 계정
    if (isAdmin) {
        dataSource = `/admin/products`;
    } else {
        dataSource = `/post`;
    }

    $.ajax({
        type: 'GET',
        url: dataSource,
        contentType: 'application/json',
        success: function (response) {
            console.log(response);
            console.log(response.length);
            alert('여기진입!!');

            $('#card-container').empty();

           // let tempHTML = addproductcard(response)
            addProductCard(response);

            // for (let i = 0; i < response.length; i++) {
            //     let card = response[i];
            //     let tempHtml = addProductCard(card);
            //     $('#card-container').append(tempHtml);
            // }
        },
        error(error, status, request) {
            if (error.status === 403) {
                $('html').html(error.responseText);
                return;
            }
            logout();
        }
    });
}

function addProductCard(response) {
    console.log(response);
    alert('새로만든 함수 진입');

    // 각 사용자별로 <div class="card"> 열기
    $('#card-container').append(`<div class="card">`);

    // 각 사용자별로 <div class="card-user-name"> 추가
    $('#card-container .card').append(`<div class="card-user-name"><h3>${response[0].user.username}</h3></div>`);

    // 각 사용자별로 <ul> 태그 열기
    $('#card-container .card').append('<ul>');

    // response 배열을 순회하며 각각의 카드를 추가
    for (let i = 0; i < response.length; i++) {
        let card = response[i];
        let tempHtml = addCard(card);

        // 각 사용자별로 <li> 태그로 감싸서 <ul> 태그 안에 추가
        $('#card-container .card ul').append(`<li>${tempHtml}<br></li>`);
    }

    // 각 사용자별로 <ul> 태그 닫기
    $('#card-container .card:last-child').append('</ul>');
    $('#card-container').append('</div>'); // 각 사용자별로 <div class="card"> 닫기
}

// addProductCard 함수에서는 <div class="product-card">로 감싸진 HTML을 반환
function addCard(product) {
    return `
        <div class="card-body">
            <div class="card-title">${product.title}</div>
            <div class="modal-content" id="${product.title}ModalContent">
                ${product.content}
            </div>
        </div>`;
}




// function addProductCard(product) {
//     console.log(product)
//     return `<div class="product-card">
//                     <div class="card-body">
//                         <div class="card-user-name">
//                             <h3>${product.user.username} </h3>
//                         </div>
//                         <div class="card-title">
//                             ${product.title}
//                         </div>
//                     </div>
//
//             </div>`;
// }

function setMyprice() {
    /**
     * 1. id가 myprice 인 input 태그에서 값을 가져온다.
     * 2. 만약 값을 입력하지 않았으면 alert를 띄우고 중단한다.
     * 3. PUT /api/product/${targetId} 에 data를 전달한다.
     *    주의) contentType: "application/json",
     *         data: JSON.stringify({myprice: myprice}),
     *         빠뜨리지 말 것!
     * 4. 모달을 종료한다. $('#container').removeClass('active');
     * 5, 성공적으로 등록되었음을 알리는 alert를 띄운다.
     * 6. 창을 새로고침한다. window.location.reload();
     */
        // 1. id가 myprice 인 input 태그에서 값을 가져온다.
    let myprice = $('#myprice').val();
    // 2. 만약 값을 입력하지 않았으면 alert를 띄우고 중단한다.
    if (myprice == '') {
        alert('올바른 가격을 입력해주세요');
        return;
    }

    // 3. PUT /api/product/${targetId} 에 data를 전달한다.
    $.ajax({
        type: 'PUT',
        url: `/api/products/${targetId}`,
        contentType: 'application/json',
        data: JSON.stringify({myprice: myprice}),
        success: function (response) {

            // 4. 모달을 종료한다. $('#container').removeClass('active');
            $('#container').removeClass('active');
            // 5. 성공적으로 등록되었음을 알리는 alert를 띄운다.
            alert('성공적으로 등록되었습니다.');
            // 6. 창을 새로고침한다. window.location.reload();
            window.location.reload();
        },
        error(error, status, request) {
            logout();
        }
    })
}
function logout() {
    // 토큰 삭제
    Cookies.remove('Authorization', {path: '/'});
    window.location.href = host + '/user/login-page';
}

function getToken() {
    let auth = Cookies.get('Authorization');

    if (auth === undefined) {
        return '';
    }

    return auth;
}
