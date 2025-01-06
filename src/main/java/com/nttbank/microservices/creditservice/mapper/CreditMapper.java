package com.nttbank.microservices.creditservice.mapper;

import com.nttbank.microservices.creditservice.dto.CreditDTO;
import com.nttbank.microservices.creditservice.model.entity.Credit;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for mapping between Credit entities and DTOs.
 */
@Mapper(componentModel = ComponentModel.SPRING)
public interface CreditMapper {

  CreditMapper INSTANCE = Mappers.getMapper(CreditMapper.class);

  Credit creditDTOToCredit(CreditDTO creditDTO);
}
