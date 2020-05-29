package com.in28minutes.microservices.currencyconversionservice.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.in28minutes.microservices.currencyconversionservice.beans.CurrencyConversionBean;
import com.in28minutes.microservices.currencyconversionservice.services.CurrencyExchangeServiceProxy;

@RestController
public class CurrencyConversionController {

	private static final String FROM_NAME_VARIABLE = "from";
	private static final String TO_NAME_VARIABLE = "to";

	@Autowired
	private CurrencyExchangeServiceProxy proxy;

	@GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {

		//I have to sent the variables in a Map
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put(FROM_NAME_VARIABLE, from);
		uriVariables.put(TO_NAME_VARIABLE, to);

		//Execute the service request
		ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate()
				.getForEntity("http://localhost:8000/currency-exchange/from/{" + FROM_NAME_VARIABLE + "}/to/{"
						+ TO_NAME_VARIABLE + "}", CurrencyConversionBean.class, uriVariables);

		CurrencyConversionBean response = responseEntity.getBody();

		return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity,
				quantity.multiply(response.getConversionMultiple()), 0);
	}

	/**
	 * Using feign is pretty much easier and cleaner to make a Rest petition
	 * it works based on ribbon and proxies
	 * @param from
	 * @param to
	 * @param quantity
	 * @return
	 */
	@GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {

		CurrencyConversionBean response = proxy.retrieveExchangeValue(from, to);

		return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity,
				quantity.multiply(response.getConversionMultiple()), 0);
	}

}
