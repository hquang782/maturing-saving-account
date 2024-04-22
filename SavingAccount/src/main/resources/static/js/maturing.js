// Lấy thông tin người dùng từ localStorage
let userInfo = JSON.parse(localStorage.getItem('userInfo'));

// Kiểm tra xem userInfo có giá trị hay không
if (!userInfo || Object.keys(userInfo).length === 0) {
    // Nếu userInfo trống, chuyển hướng đến trang đăng nhập
    window.location.href = "/login";
}
document.addEventListener("DOMContentLoaded", function () {
    const progressListItems =
        document.querySelectorAll("#progressbar li");
    const progressBar =
        document.querySelector(".progress-bar");
    let currentStep = 0;
    let selectedSavingsAccountId = localStorage.getItem("selectedSavingsAccountId");

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


    // fetch to show status
    const loadingImg0 = document.getElementById(`step0Loading`);
    fetch(`http://localhost:8080/api/v1/task/${selectedSavingsAccountId}`,{
        method:'POST',
    }).then((response)=>{
        if(!response.ok){
            throw new Error('Network response was not ok');
        }
        else{
            loadingImg0.style.display='none';
            updateStatus(0,true);
        }

    }).catch(error=>{
        alert(error);
        loadingImg0.style.display='none';
        updateStatus(0,false);
    });

    function end(){
        const loadingImg3 = document.getElementById(`step3Loading`);
        fetch(`http://localhost:8080/api/v1/task/${selectedSavingsAccountId}`,{
            method:'DELETE',
        }).then((response)=>{
            if(!response.ok){
                throw new Error('Network response was not ok');
            }
            else{
                loadingImg3.style.display='none';
                updateStatus(3,true);
            }

        }).catch(error=>{
            alert(error);
            loadingImg3.style.display='none';
            updateStatus(3,false);
        });
    }


    function updateProgress() {
        const percent =
            (currentStep / (progressListItems.length - 1)) * 100;
        progressBar.style.width = percent + "%";

        progressListItems.forEach((item, index) => {
            if (index === currentStep) {
                item.classList.add("active");
            } else {
                item.classList.remove("active");
            }
        });
    }

    function showStep(stepIndex) {
        const steps =
            document.querySelectorAll(".step-container fieldset");
        steps.forEach((step, index) => {
            if (index === stepIndex) {
                step.style.display = "block";
            } else {
                step.style.display = "none";
            }
        });
    }

    function nextStep() {
        if (currentStep < progressListItems.length - 1) {
            if(currentStep===0){
                const password = document.getElementById("password").value;
                const loadingImg1 = document.getElementById(`step1Loading`);
                fetch(`http://localhost:8081/api/auth/verifyPassword`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        username: userInfo.account.username,
                        password: password
                    }) // Gửi mật khẩu đến server
                }).then(response => {
                    if (!response.ok) {
                        throw new Error('Failed to verify password');
                    }
                    loadingImg1.style.display='none';
                    updateStatus(1,true);
                    alert('Xác thực mật khẩu thành công');
                    currentStep++;
                    showStep(currentStep);
                    updateProgress();
                }).catch(error => {
                    console.error('Error:', error);
                    // Xử lý khi xảy ra lỗi
                    alert('Đã xảy ra lỗi khi xác thực mật khẩu');
                    loadingImg1.style.display='none';
                    updateStatus(1,false);
                });
            }
        }
    }

    function prevStep() {
        if (currentStep > 0) {
            currentStep--;
            showStep(currentStep);
            updateProgress();
        }
    }

    const nextStepButtons =
        document.querySelectorAll(".next-step");
    const prevStepButtons =
        document.querySelectorAll(".previous-step");

    nextStepButtons.forEach((button) => {
        button.addEventListener("click", nextStep);
    });

    prevStepButtons.forEach((button) => {
        button.addEventListener("click", prevStep);
    });


// Hiển thị mã OTP lên trang
    document.getElementById('otpDisplay').innerText = generateOTP();

// Lấy đối tượng nút "Xác nhận" bằng id
    let confirmButton = document.getElementById('confirmButton');

// Thêm sự kiện click vào nút "Xác nhận"
    confirmButton.addEventListener('click', function (event) {
        // Ngăn chặn hành vi mặc định của nút "submit" là gửi form
        event.preventDefault();
        // Gọi hàm xác nhận OTP
        verifyOTP();
    });

// forcis trỏ chuột vào ô đầu tiên
    let m = document.querySelectorAll('input[type="text"]');
    m[0].focus();
// Lấy tất cả các ô input
    const inputs = document.querySelectorAll('input[type="text"]');

// Duyệt qua từng ô input
    inputs.forEach((input, index) => {
        // Thêm sự kiện input cho mỗi ô input
        inputs[0].focus();
        input.addEventListener('input', function () {
            // Nếu độ dài của giá trị trong ô input đạt tối đa
            if (this.value.length === parseInt(this.getAttribute('maxlength'))) {
                // Chuyển tới ô input tiếp theo
                if (index < inputs.length - 1) {
                    inputs[index + 1].focus();
                }
            }
        });
    });

// Hàm tạo số OTP ngẫu nhiên có 6 chữ số
    function generateOTP() {
        console.log('hello');
        var otp = '';
        for (var i = 0; i < 6; i++) {
            otp += Math.floor(Math.random() * 10); // Tạo số từ 0 đến 9
        }
        return otp;
    }


// Hàm xác nhận OTP
    function verifyOTP() {
        // Lấy giá trị từ các ô nhập
        let digit1 = document.getElementById('digit1').value;
        let digit2 = document.getElementById('digit2').value;
        let digit3 = document.getElementById('digit3').value;
        let digit4 = document.getElementById('digit4').value;
        let digit5 = document.getElementById('digit5').value;
        let digit6 = document.getElementById('digit6').value;

        // Ghép các giá trị lại thành một mã OTP
        let otpEntered = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;

        // Lấy OTP từ localStorage
        let otpStored = document.getElementById('otpDisplay').innerText;

        // So sánh mã OTP nhập vào với mã OTP được tạo ra
        if (otpEntered === otpStored) {
            const loadingImg2 = document.getElementById(`step2Loading`);
            fetch(`http://127.0.0.1:8080/api/v1/savings-accounts/${selectedSavingsAccountId}`, {
                method: 'DELETE'
            })
                .then(function (response) {
                    // Kiểm tra mã trạng thái của phản hồi
                    if (response.ok) {
                        // Phản hồi thành công từ API cập nhật thông tin tài khoản
                        alert('Cập nhật thông tin tài khoản thành công!');
                        loadingImg2.style.display='none';
                        updateStatus(2,true);
                        fetchAndUpdateUserInfo(userInfo);
                        // Chuyển hướng người dùng sau khi tạo tài khoản và cập nhật thông tin thành công
                        currentStep++;
                        showStep(currentStep);
                        updateProgress();
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
                    loadingImg2.style.display='none';
                    updateStatus(2,false);
                });
        } else {
            alert('Xác nhận không thành công. Vui lòng thử lại.');
        }
    }
});

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
            var formattedUserInfo = {
                fullName: data.fullName,
                age: data.age,
                gender: data.gender,
                dob: data.dob,
                address: data.address,
                email: data.email,
                phoneNumber: data.phoneNumber,
                identificationNumber: data.identificationNumber,
                bankAccountNumber: data.bankAccountNumber,
                account: {
                    id: data.account.id,
                    username: data.account.username,
                    balance: data.account.balance,
                    roles: data.account.roles
                }
            };
            // Lưu userInfo vào localStorage
            localStorage.setItem('userInfo', JSON.stringify(formattedUserInfo));
            // Gọi hàm để cập nhật thông tin trên giao diện người dùng nếu cần
        })
        .catch(error => {
            console.error('Error:', error);
            // Xử lý khi xảy ra lỗi
            alert('Đã xảy ra lỗi khi lấy thông tin mới của khách hàng');
        });
}