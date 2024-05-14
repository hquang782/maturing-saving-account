package com.example.task_service.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessRequest {
    private long customerId;
    private long savingAccountId;
    private String userName;
    private String passwordVerify;
}
