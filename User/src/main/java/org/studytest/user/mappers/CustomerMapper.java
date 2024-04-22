package org.studytest.user.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.studytest.user.models.Customer;
import org.studytest.user.payload.CustomerDTO;


@Component
public class CustomerMapper {

    private final ModelMapper modelMapper;

    public CustomerMapper() {
        this.modelMapper = new ModelMapper();
    }

    // Chuyển đổi từ Customer thành CustomerDTO
    public CustomerDTO convertToDTO(Customer customer) {
        return modelMapper.map(customer, CustomerDTO.class);
    }

    // Chuyển đổi từ CustomerDTO thành Customer
    public Customer convertToEntity(CustomerDTO customerDTO) {
        return modelMapper.map(customerDTO, Customer.class);
    }
}