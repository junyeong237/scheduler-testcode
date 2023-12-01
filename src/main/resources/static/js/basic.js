const host = 'http://' + window.location.host;
let targetId;

$(document).ready(function () {
    const auth = getToken();
    if (auth !== undefined && auth !== '') {
        $('#login-true').show();
        $('#login-false').hide();
        $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
            jqXHR.setRequestHeader('Authorization', auth);
            // ajax 요청헤더에 set
        });
    } else {
        $('#login-true').hide();
        $('#login-false').show();
        showProduct();
        return; //지워도 될듯하다 //이거 지우면 밑에 .ajax가 실행되는듯
    }

    $.ajax({
        type: 'GET',
        url: `/user-info`,
        contentType: 'application/json',
    })
        .done(function (res, status, xhr) {

            const username = res.username;

            if (!username) {
                window.location.href = '/user/login-page';
                return;
            }
            console.log(username)
            $('#username').text(username);

            showProduct();

        })
        .fail(function (jqXHR, textStatus) {
            logout();
        });

});


function showProduct() {
    /**
     * 관심상품 목록: #product-container
     * 검색결과 목록: #search-result-box
     * 관심상품 HTML 만드는 함수: addProductItem
     */

    let dataSource = null;

    // admin 계정


    dataSource = `/alluser`;


    $.ajax({
        type: 'GET',
        url: dataSource,
        contentType: 'application/json',
        success: function (response) {
            //var res = response.data;
            console.log(response);
            console.log(response.length);

            $('#card-container').empty();

            renderCards(response);

            document.getElementById('close-popup').addEventListener('click', closePopup);

            document.getElementById('close-edit-popup').addEventListener('click', closePopupEdit);

            document.getElementById('add-edit-popup').addEventListener('click', closePopupAdd);

            document.getElementById('reply-close-popup').addEventListener('click', closePopupReply)

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

function renderCards(response) {
    const cardContainer = document.getElementById('card-container');
    cardContainer.innerHTML = ''; // Clear existing cards

    response.forEach(user => {
        const userCard = document.createElement('div');
        userCard.className = 'user-card';
        userCard.innerHTML = `<h3>${user.username}</h3>`;
        user.postList.forEach(postList => {
            console.log(postList);
            const postTitle = document.createElement('div');
            postTitle.className = 'task-title';
            postTitle.textContent = postList.title;

            // Add click event to show popup on title click
            postTitle.addEventListener('click', () => showPopup(postList.title, postList.content, postList));
            const deleteButton = document.createElement('button');
            deleteButton.className = 'delete-card-btn';
            deleteButton.textContent = '게시글 삭제하기';
            deleteButton.addEventListener('click', () => deleteTask(postList.id));


            userCard.appendChild(postTitle);
            userCard.appendChild(deleteButton);
        });
        userCard.appendChild(createAddTaskButton());
        cardContainer.appendChild(userCard);

    });


}

function createAddTaskButton() {
    const addTaskBtn = document.createElement('div');
    addTaskBtn.textContent = '추가하기';
    addTaskBtn.classList.add('add-task-btn');
    addTaskBtn.addEventListener('click', showAddTaskPopup);
    return addTaskBtn;
}

// Function to show popup with title and content
function showPopup(title, content, postList) {
    const overlay = document.getElementById('overlay');
    const popup = document.getElementById('popup');
    const popupTitle = document.getElementById('popup-title');
    const popupContent = document.getElementById('popup-content');
    const popupDate = document.getElementById('popup-date');
    const replyContainer = document.getElementById('reply-container');
    const addCommentBtn = document.getElementById('add-comment-btn');
    const commentFormContainer = document.getElementById('comment-form-container');
    const commentText = document.getElementById('comment-text');
    const submitCommentBtn = document.getElementById('submit-comment-btn');
    const editButton = document.getElementById('edit-button'); // 수정하기 버튼 추가

    popupTitle.textContent = title;
    popupContent.textContent = content;
    popupDate.textContent = postList.createdAt;

    renderReplyList(replyContainer, postList.replyList);

    addCommentBtn.addEventListener('click', () => {
        commentFormContainer.style.display = 'block';
        addCommentBtn.style.display = 'none';
    });

    submitCommentBtn.addEventListener('click', () => {
        addReply(commentText, postList.id);

    });

    const completionCheckbox = document.getElementById('completion-checkbox');
    completionCheckbox.checked = postList.finished; // Set initial state based on postList data


    completionCheckbox.addEventListener('change', function() {
        updatefinishedPost(postList);
    });

    editButton.addEventListener('click', () => {
        showEditPopup(postList);
    });



    overlay.style.display = 'block';
    popup.style.display = 'block';
}

function showEditPopup(postList){
    const token = getToken();
    if(token) {
        const overlayEdit = document.getElementById('edit-overlay');
        const popup = document.getElementById('edit-popup');
        const popupTitle = document.getElementById('edit-popup-title');
        const popupContent = document.getElementById('edit-popup-content');
        const poputCloseBtn = document.getElementById('close-edit-popup');
        popup.innerHTML = '';
        const editPopup = document.createElement('div');
        const editTitleInput = document.createElement('input');
        const editContentInput = document.createElement('textarea');
        const saveChangesBtn = document.createElement('button');

        editTitleInput.value = postList.title;
        editContentInput.value = postList.content;

        editPopup.appendChild(poputCloseBtn);
        editPopup.appendChild(document.createTextNode('수정하기'));
        editPopup.appendChild(document.createElement('br'));
        editPopup.appendChild(document.createTextNode('제목: '));
        editPopup.appendChild(editTitleInput);
        editPopup.appendChild(document.createElement('br'));
        editPopup.appendChild(document.createTextNode('내용: '));
        editPopup.appendChild(editContentInput);
        editPopup.appendChild(document.createElement('br'));
        editPopup.appendChild(saveChangesBtn);

        saveChangesBtn.textContent = '변경 사항 저장';

        popup.appendChild(editPopup);


        saveChangesBtn.addEventListener('click', () => {
            // Your logic to save the changes, update the postList data, and close the popup
            const newTitle = editTitleInput.value;
            const newContent = editContentInput.value;

            // Assuming you have an endpoint like `/post/update/:id` for updating a post
            $.ajax({
                url: `/post/update/${postList.id}`,
                type: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify({title: newTitle, content: newContent}),
                success: function (result) {
                    // Handle success, e.g., update the UI to reflect the changes
                    console.log(result);

                    // Update the UI to show the edited title and content
                    popupTitle.textContent = newTitle;
                    popupContent.textContent = newContent;

                    // Close the edit popup
                    //closePopup();
                    window.location.href = '/';
                },
                error: function (error) {
                    console.error('Error:', error);
                }
            });
        });


        popup.style.display = 'block';
        overlayEdit.style.display = 'block';


    }
    else {
        alert('로그인을 해주세요');
    }


}

function renderReplyList(container, replyList) {
    container.innerHTML = ''; // Clear existing content
    if (replyList.length === 0) {
        const noReplyMessage = document.createElement('p');
        noReplyMessage.textContent = 'No replies yet.';
        container.appendChild(noReplyMessage);
    } else {
        replyList.forEach(reply => {
            const replyElement = document.createElement('div');
            replyElement.className = 'reply';
            const replyContent = document.createElement('span');
            replyContent.innerHTML = `<strong>${reply.username}:</strong> ${reply.content}`;
            const replyDate = document.createElement('span');
            replyDate.innerText = ' 작성시간:' + reply.createdAt ;
            const editButton = document.createElement('button');
            editButton.textContent = '댓글수정하기';
            editButton.addEventListener('click', () => showEditReplyPopup(reply, replyContent));
            const deleteButton = document.createElement('button');
            deleteButton.textContent = '댓글삭제';
            deleteButton.addEventListener('click', () => deleteReply(reply.id));
            replyElement.appendChild(replyContent);
            replyElement.appendChild(replyDate);
            replyElement.appendChild(editButton);
            replyElement.appendChild(deleteButton);

            container.appendChild(replyElement);
        });
    }
}

function addReply(commentText, postId) {
    const token = getToken();
    const content = commentText.value;

    // Create a data object with the title and content
    const data = {
        content: content
    };

    if (token) {

        $.ajax({
            url: `/reply/create/${postId}`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (result) {
                //closePopup();

                window.location.href = '/';
            },
            error: function (error) {
                // 서버 요청 중 오류가 발생했을 때의 처리
                console.error('Error:', error);
            }
        });
    }
    else {
        alert('로그인을 해주세요.');
    }


}

function showEditReplyPopup(reply) {
    const token = getToken();
    if(token) {

        const editReplyPopup = document.getElementById('edit-reply-popup');
        const editContentInput = document.getElementById('edit-content-input');
        const saveChangesBtn = document.getElementById('save-changes-btn');
        //const saveChangesBtn = document.createElement('button');

        editContentInput.value = reply.content;

        editReplyPopup.style.display = 'block';
        saveChangesBtn.textContent = '변경 사항 저장';

        saveChangesBtn.addEventListener('click', () => {
            // Your logic to save the changes, update the postList data, and close the popup
            const newContent = editContentInput.value;

            // Assuming you have an endpoint like `/post/update/:id` for updating a post
            $.ajax({
                url: `/reply/update/${reply.id}`,
                type: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify({content: newContent}),
                success: function (result) {
                    // Handle success, e.g., update the UI to reflect the changes
                    console.log(result);

                    // Update the UI to show the edited title and content
                    editContentInput.textContent = newContent;

                    // Close the edit popup
                    //closePopup();
                    window.location.href = '/';
                },
                error: function (error) {
                    console.error('Error:', error);
                }
            });
        });

    }
    else {
        alert('로그인을 해주세요');
    }

    // (이후 코드)
}



// Function to close popup
function closePopup() {
    const overlay = document.getElementById('overlay');
    const popup = document.getElementById('popup');

    overlay.style.display = 'none';
    popup.style.display = 'none';
}

function closePopupEdit() {
    const overlay = document.getElementById('edit-overlay');
    const popup = document.getElementById('edit-popup');

    overlay.style.display = 'none';
    popup.style.display = 'none';
}

function closePopupAdd() {
    const overlay = document.getElementById('add-overlay');
    const popup = document.getElementById('add-task-popup');

    overlay.style.display = 'none';
    popup.style.display = 'none';
    window.location.href = '/';
}

function closePopupReply(){
    const popup = document.getElementById('edit-reply-popup');

    popup.style.display = 'none';


}


function deleteReply(replyId) {
    const token = getToken();
    if(token) {
        // Assuming you have an endpoint like `/reply/delete/:id` for deleting a reply
        $.ajax({
            url: `/reply/delete/${replyId}`,
            type: 'DELETE',
            success: function (result) {
                // Handle success, e.g., remove the deleted reply from the UI
                //closePopup();

                window.location.href = '/';
            },
            error: function (error) {
                console.error('Error:', error);
            }
        });
    }
    else {
        alert('로그인을 해주세요');
    }
}

// Function to show popup for adding a new task
    function showAddTaskPopup() {
        const token = getToken();
        if(token){
            const overlay = document.getElementById('overlay');
            const popup = document.getElementById('add-task-popup');

            overlay.style.display = 'block';
            popup.style.display = 'block';
        }
        else{
            alert('로그인을 하셔야합니다.');
        }

    }

// Function to add a new task to the user-card
    function addTask() {

        const token = getToken();
        if(token) {
            const titleInput = document.getElementById('task-title-input');
            const contentInput = document.getElementById('task-content-input');

            const title = titleInput.value;
            const content = contentInput.value;

            // Create a data object with the title and content
            const data = {
                title: title,
                content: content
            };

            $.ajax({
                url: '/post/create',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function (result) {
                    //closePopup();

                    window.location.href = '/';
                },
                error: function (error) {
                    // 서버 요청 중 오류가 발생했을 때의 처리
                    console.error('Error:', error);
                }
            });
        }
        else{
            alert('로그인을 하셔야합니다.')
        }


    }



function updatefinishedPost(postList) {
    const token = getToken();
    // Update the finished status in the postList
    postList.finished = this.checked;

    if(token) {
        // Assuming you have an endpoint like `/post/update/:id` for updating a post
        $.ajax({
            url: `/post/updatefinished/${postList.id}`,
            type: 'GET',
            contentType: 'application/json',
            //data: JSON.stringify({ finished: postList.finished }),
            success: function (result) {
                // Handle success, e.g., update the UI to reflect the finished status

                console.log(result);

                //closePopup();

                window.location.href = '/';

            },
            error: function (error) {
                console.error('Error:', error);
            }
        });
    }
    else {
        alert('로그인을 해주세요');
    }
}
    function deleteTask(id) {
        const token = getToken();
        if(token) {
            $.ajax({
                url: `/post/delete/${id}`,
                type: 'DELETE',
                contentType: 'application/json',
                success: function (result) {
                    // Success! Task deleted on the server
                    console.log(result);

                    //closePopup();

                    window.location.href = '/';
                },
                error: function (error) {
                    // Error handling if the request fails
                    console.error('Error:', error);
                }
            });
        }
        else{
            alert('로그인을 해주세요.');
        }
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



