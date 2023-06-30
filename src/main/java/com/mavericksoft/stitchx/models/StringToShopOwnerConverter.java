package com.mavericksoft.stitchx.models;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@Component
public class StringToShopOwnerConverter implements Converter<String, ShopOwner> {

	@Override
	@SneakyThrows
	public ShopOwner convert(String source) {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(source, ShopOwner.class);
	}

}
