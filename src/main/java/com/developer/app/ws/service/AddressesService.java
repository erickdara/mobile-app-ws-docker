package com.developer.app.ws.service;

import com.developer.app.ws.shared.dto.AddressDTO;

import java.util.List;

public interface AddressesService {
    List<AddressDTO> getAddresses(String userId);

    AddressDTO getAddress(String addressId);
}
