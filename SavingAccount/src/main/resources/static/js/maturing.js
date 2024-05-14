// Lấy thông tin người dùng từ localStorage
let userInfo = JSON.parse(localStorage.getItem('userInfo'));

// Kiểm tra xem userInfo có giá trị hay không
if (!userInfo || Object.keys(userInfo).length === 0) {
    // Nếu userInfo trống, chuyển hướng đến trang đăng nhập
    window.location.href = "http://localhost:8081/login";
}
document.addEventListener("DOMContentLoaded", function () {

    let selectedSavingsAccountId = localStorage.getItem("selectedSavingsAccountId");
    const loadingImg1 = document.getElementById(`step1Loading`);
    const loadingImg2 = document.getElementById(`step2Loading`);
    const loadingImg4 = document.getElementById(`step4Loading`);
    const loadingImg3 = document.getElementById(`step3Loading`);

    const requestData = {
        passwordVerify: localStorage.getItem("passwordVerify"),
        savingAccountId: selectedSavingsAccountId,
        customerId: userInfo.id,
        userName: userInfo.account.username,
    };
    console.log(requestData);
    // show status
    function updateStatus(step, success) {
        const statusSpan = document.getElementById(`step${step}Status`);
        if (success) {
            statusSpan.textContent = "Success";
            statusSpan.classList.add('success');
        } else {
            statusSpan.textContent = "Failure - end the process";
            statusSpan.classList.add('failure');
        }
    }

    // set time delay
    function delay(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    // fetch to show status
    start();

    function start() {
        setTimeout(() => {
            const loadingImg0 = document.getElementById(`step0Loading`);
            fetch(`http://localhost:8082/api/v1/task/${selectedSavingsAccountId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestData),
            }).then(async (response) => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                } else {
                    loadingImg0.style.display = 'none';
                    updateStatus(0, true);
                }
                await delay(2000);
                getStatus();
                // verify();
            }).catch(error => {
                alert(error);
                loadingImg0.style.display = 'none';
                updateStatus(0, false);
            });
        }, 2000)
    }


    function end() {
        fetch(`http://localhost:8082/api/v1/task/${selectedSavingsAccountId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData),
        }).then((response) => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            } else {
                loadingImg3.style.display = 'none';
                updateStatus(3, true);
            }

        }).catch(error => {
            alert(error);
            loadingImg3.style.display = 'none';
            updateStatus(3, false);
        });
    }

    // test task api
    function getStatus() {

        fetch("http://localhost:8082/api/v1/task", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData),
        }).then(response => {
            return response.text();
        })
            .then(data => {
                // Xử lý dữ liệu trả về
                if (data.includes("Verify Failed!")) {
                    // Xử lý trường hợp xác thực mật khẩu thất bại
                    console.log("Verify Failed!");
                    loadingImg1.style.display = 'none';
                    loadingImg2.style.display = 'none';
                    loadingImg4.style.display = 'none';
                    updateStatus(1, false);
                    loadingImg3.style.display = 'none';
                    updateStatus(3, true);
                } else if (data.includes("Maturing Failed!")) {
                    // Xử lý trường hợp tất toán thất bại
                    console.log("Maturing Failed!");
                    loadingImg2.style.display = 'none';
                    loadingImg4.style.display = 'none';
                    updateStatus(2, false);
                    loadingImg3.style.display = 'none';
                    updateStatus(3, true);
                } else {
                    // Xử lý trường hợp thành công
                    console.log(data);
                    loadingImg1.style.display = 'none';
                    updateStatus(1, true);
                    setTimeout(() => {
                        loadingImg2.style.display = 'none';
                        updateStatus(2, true);
                        fetchAndUpdateUserInfo(userInfo);
                        setTimeout(() => {
                            end();
                        }, 2000);
                    }, 2000);
                }
            })
            .catch(error => {
                console.error('There was a problem with the fetch operation:', error);
            });
    }


    function verify() {

        const password = localStorage.getItem("passwordVerify");
        const loadingImg1 = document.getElementById(`step1Loading`);
        fetch(`http://localhost:8083/api/auth/verifyPassword`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: userInfo.account.username,
                password: password
            }) // Gửi mật khẩu đến server
        }).then(async (response) => {
            if (!response.ok) {
                throw new Error('Failed to verify password');
            }
            loadingImg1.style.display = 'none';
            updateStatus(1, true);
            await delay(2000);
            maturing();
            //     xóa mật khẩu khỏi local storage TODO

        }).catch(error => {
            console.error('Error:', error);
            // Xử lý khi xảy ra lỗi
            alert('Đã xảy ra lỗi khi xác thực mật khẩu');
            loadingImg1.style.display = 'none';
            updateStatus(1, false);
        });

    }

    // maturing saving account
    function maturing() {

        const loadingImg2 = document.getElementById(`step2Loading`);
        fetch(`http://127.0.0.1:8080/api/v1/savings-accounts/${selectedSavingsAccountId}`, {
            method: 'DELETE'
        })
            .then(async (response) => {
                // Kiểm tra mã trạng thái của phản hồi
                if (response.ok) {
                    // Phản hồi thành công từ API cập nhật thông tin tài khoản
                    // alert('tất toán tài khoản thành công!');
                    loadingImg2.style.display = 'none';
                    updateStatus(2, true);
                    await delay(2000);
                    fetchAndUpdateUserInfo(userInfo);
                    await delay(2000);
                    end();
                } else {
                    // Xử lý khi có lỗi từ server
                    throw new Error('Có lỗi từ server khi cập nhật thông tin tài khoản.');
                }
            })
            .catch(function (error) {
                // Xử lý lỗi khi gửi yêu cầu hoặc nhận phản hồi
                console.error('Có lỗi xảy ra:', error);
                alert('Có lỗi xảy ra. Vui lòng thử lại sau.');
                loadingImg2.style.display = 'none';
                updateStatus(2, false);
            });

    }

    function fetchAndUpdateUserInfo(userInfo) {
        // Thực hiện yêu cầu GET mới để lấy thông tin mới của khách hàng
        fetch('http://127.0.0.1:8081/api/v1/customers/' + userInfo.account.id, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch customer information');
                }
                // Parse dữ liệu JSON nhận được từ response
                return response.json();
            })
            .then(data => {
                // Định dạng lại dữ liệu thành cấu trúc userInfo
                let formattedUserInfo = {
                    id: data.id,
                    fullName: data.fullName,
                    age: data.age,
                    gender: data.gender,
                    dob: data.dob,
                    address: data.address,
                    email: data.email,
                    phoneNumber: data.phoneNumber,
                    identificationNumber: data.identificationNumber,
                    bankAccountNumber: data.bankAccountNumber,
                    balance: data.balance,
                    account: {
                        id: data.account.id,
                        username: data.account.username,
                    }
                };
                // Lưu userInfo vào localStorage
                localStorage.setItem('userInfo', JSON.stringify(formattedUserInfo));
                // Gọi hàm để cập nhật thông tin trên giao diện người dùng nếu cần
                const userData = JSON.stringify(formattedUserInfo);
                document.cookie = `userInfo=${encodeURIComponent(userData)}; path=/`;
                loadingImg4.style.display = 'none';
                updateStatus(4, true);
                // alert("Cập nhật số dư thành công");
            })
            .catch(error => {
                console.error('Error:', error);
                // Xử lý khi xảy ra lỗi
                alert('Đã xảy ra lỗi khi lấy thông tin mới của khách hàng');
            });
    }
});

