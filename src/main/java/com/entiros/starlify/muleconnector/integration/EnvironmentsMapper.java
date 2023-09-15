package com.entiros.starlify.muleconnector.integration;


import com.entiros.starlify.muleconnector.api.dto.Account;
import com.entiros.starlify.muleconnector.api.dto.Environment;
import org.mapstruct.Mapper;


import java.util.List;

@Mapper
public interface EnvironmentsMapper {

    default List<Environment> map(Account account){
        return account.environments;
    }
}
