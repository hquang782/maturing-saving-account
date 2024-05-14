
// Kiểm tra xem userInfo có giá trị hay không
const userInfoFromCookie = getSavedUserDataFromCookie();

let userInfo = JSON.parse(localStorage.getItem('userInfo'));
if (userInfoFromCookie) {
    // Lưu dữ liệu vào localStorage
    localStorage.setItem('userInfo', JSON.stringify(userInfoFromCookie));
}else{
    // Nếu userInfo trống, chuyển hướng đến trang đăng nhập
    window.location.href = "http://localhost:8081/login";
    console.log('error')
}
function getSavedUserDataFromCookie() {
    const cookieData = document.cookie
        .split('; ')
        .find(row => row.startsWith('userInfo='));
    if (cookieData) {
        const userData = cookieData.split('=')[1];
        return JSON.parse(decodeURIComponent(userData));
    }
    return null;
}

document.addEventListener("DOMContentLoaded", function () {

    const savingsList = document.getElementById("savingsList");
    const savingsInfo = document.getElementById("savingsInfo");
    const modal = document.getElementById("modal");
    const closeModal = document.getElementById("closeModal");
    const withdrawButton = document.getElementById("withdraw");


    let selectedSavingsAccountId; // Biến để lưu trữ ID của sổ tiết kiệm được chọn
    let savingsAccounts = []; // Mảng để lưu trữ danh sách sổ tiết kiệm
    // lấy customer id
    let identificationNumber = userInfo.identificationNumber

    // Function to render savings accounts list
    function renderSavingsList() {
        // Clear the existing list
        savingsList.innerHTML = "";
        // Render the updated list
        savingsAccounts.forEach(saving => {
            const listItem = document.createElement("li");
            listItem.textContent = saving.accountName;
            listItem.dataset.id = saving.id; // Set data-id attribute with the ID
            listItem.addEventListener("click", () => {
                selectedSavingsAccountId = saving.id;
                showSavingsInfo(saving);
            });
            savingsList.appendChild(listItem);
        });
    }

    // Fetch data from server
    fetch(`http://127.0.0.1:8080/api/v1/savings-accounts/list/${identificationNumber}`)
        .then(response => response.json())
        .then(data => {
            savingsAccounts = data; // Save the fetched data to the array
            // Render the savings accounts list
            renderSavingsList();
            console.log('fetch done')
        })
        .catch(error => console.error("Error fetching data:", error));

    // Show savings info in modal
    function showSavingsInfo(saving) {
        savingsInfo.innerHTML = `
            <p>Tên sổ tiết kiệm: ${saving.accountName}</p>
            <p>Số tiền: ${formatCurrency(saving.depositAmount)} VND</p>
            <p>Lãi suất: ${saving.interestRateValue}%</p>
            <p>Ngày gửi: ${saving.depositDate}</p>
            <p>Kỳ hạn: ${saving.term}</p>
            <p>Ngày đáo hạn: ${saving.maturityDate}</p>
        `;
        console.log(selectedSavingsAccountId)
        modal.style.display = "block";
    }

    // Close modal
    closeModal.addEventListener("click", () => {
        modal.style.display = "none";
    });

    const closeButton1 = document.querySelector("#modal .close");
    closeButton1.addEventListener("click", () => {
        modal.style.display = "none";
    });
    // Withdraw button action
    withdrawButton.addEventListener("click", () => {
        if (!selectedSavingsAccountId) {
            alert("Vui lòng chọn một sổ tiết kiệm.");
            return;
        }
        localStorage.setItem("selectedSavingsAccountId",selectedSavingsAccountId);

        // Hiển thị dialog nhập mật khẩu
        const passwordDialog = document.getElementById("passwordDialog");
        passwordDialog.style.display = "block";

        // Đóng dialog khi nhấn nút đóng
        const closeButton = document.querySelector("#passwordDialog .close");
        closeButton.addEventListener("click", () => {
            passwordDialog.style.display = "none";
        });

        // Xác nhận mật khẩu khi nhấn nút xác nhận
        const confirmButton = document.getElementById("confirmButton");
        confirmButton.addEventListener("click", () => {
            const password = document.getElementById("passwordInput").value;

            // Kiểm tra xem mật khẩu đã nhập có hợp lệ không
            if (password === '') {
                alert("Vui lòng nhập mật khẩu.");
                return;
            }

            localStorage.setItem("passwordVerify",password);
            window.location.href = "/Process";
        });
    });


    // Close modal when clicking outside of it
    window.addEventListener("click", (event) => {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });
});

function redirectToHome() {
    window.location.href = "http://localhost:8083/home";
}


function formatCurrency(amount) {
    // Chuyển đổi số tiền sang chuỗi
    var amountString = amount.toString();

    // Tạo một biến để lưu trữ chuỗi đã định dạng
    var formattedAmount = '';

    // Lặp qua từng ký tự của chuỗi số tiền
    for (var i = amountString.length - 1, j = 1; i >= 0; i--, j++) {
        formattedAmount = amountString.charAt(i) + formattedAmount;
        if (j % 3 === 0 && i > 0) {
            formattedAmount = ',' + formattedAmount;
        }
    }

    return formattedAmount;
}