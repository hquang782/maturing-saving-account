package org.studytest.saving_account.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="Maturing Saving Account", description = "task service")
@RestController
@RequestMapping("/api/v1/task")
public class MaturingSavingAccount {
    @PostMapping("/{id}")
    public ResponseEntity<String> createMaturingSavingAccount(@PathVariable("id") Long id) {
        // Xử lý logic để tạo mới một quy trình tất toán sổ tiết kiệm với thông tin từ savingAccountInfo
        // Return ResponseEntity với HttpStatus.CREATED nếu thành công
        // Hoặc ResponseEntity với HttpStatus.INTERNAL_SERVER_ERROR nếu có lỗi xảy ra
        return new ResponseEntity<>("Start Process", HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelMaturingSavingAccount(@PathVariable("id") Long id) {
        // Xử lý logic để hủy một quy trình tất toán sổ tiết kiệm với id đã cung cấp
        // Return ResponseEntity với HttpStatus.OK nếu thành công
        // Hoặc ResponseEntity với HttpStatus.INTERNAL_SERVER_ERROR nếu có lỗi xảy ra
        return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getMaturingSavingAccountStatus(@PathVariable("id") Long id) {
        // Xử lý logic để truy vấn trạng thái của một phiên bản thực thi của quy trình tất toán sổ tiết kiệm với id đã cung cấp
        // Return ResponseEntity với thông tin trạng thái nếu thành công
        // Hoặc ResponseEntity với HttpStatus.NOT_FOUND nếu không tìm thấy
        return new ResponseEntity<>("Status: In progress", HttpStatus.OK);
    }
}
