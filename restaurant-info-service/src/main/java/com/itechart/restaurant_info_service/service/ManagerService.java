package com.itechart.restaurant_info_service.service;

import com.itechart.restaurant_info_service.config.DeletingUserConfig;
import com.itechart.restaurant_info_service.dto.IdentityRegistrationDTO;
import com.itechart.restaurant_info_service.dto.ManagerRegistrationInfoDTO;
import com.itechart.restaurant_info_service.exception.GettingInfoException;
import com.itechart.restaurant_info_service.exception.ManagerRegistrationException;
import com.itechart.restaurant_info_service.model.Manager;
import com.itechart.restaurant_info_service.repository.ManagerRepository;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Optional;

@Service
@Validated
@AllArgsConstructor
public class ManagerService {
    private final RabbitTemplate rabbitTemplate;
    private final ManagerRepository managerRepository;
    private final RestaurantService restaurantService;

    @LoadBalanced
    private final RestTemplate restTemplate;

    @Transactional
    public Manager registerManager(@Valid ManagerRegistrationInfoDTO managerRegistrationInfoDTO) throws ManagerRegistrationException {
        Long userId = 0L;

        try {
            final String IDENTITY_REGISTER_URL = "http://IDENTITY-SERVICE/register";
            final String ROLE_MANAGER = "ROLE_MANAGER";
            IdentityRegistrationDTO identityRegistrationDTO = IdentityRegistrationDTO.builder()
                    .email(managerRegistrationInfoDTO.getEmail())
                    .password(managerRegistrationInfoDTO.getPassword())
                    .firstName(managerRegistrationInfoDTO.getFirstName())
                    .lastName(managerRegistrationInfoDTO.getLastName())
                    .role(ROLE_MANAGER)
                    .build();

            ResponseEntity<IdentityRegistrationDTO> response = restTemplate.
                    postForEntity(IDENTITY_REGISTER_URL, identityRegistrationDTO, IdentityRegistrationDTO.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ManagerRegistrationException("Identity service couldn't register the manager");
            }

            userId = response.getBody().getId();

            Manager manager = Manager.builder()
                    .userId(userId)
                    .email(managerRegistrationInfoDTO.getEmail())
                    .firstName(managerRegistrationInfoDTO.getFirstName())
                    .lastName(managerRegistrationInfoDTO.getLastName())
                    .phoneNumber(managerRegistrationInfoDTO.getPhoneNumber())
                    .role(ROLE_MANAGER)
                    .build();

            manager = managerRepository.save(manager);
            restaurantService.createDefaultRestaurant(manager, managerRegistrationInfoDTO.getRestaurantName());

            return manager;
        } catch (Throwable ex) {
            rabbitTemplate.convertAndSend(
                    DeletingUserConfig.EXCHANGE,
                    DeletingUserConfig.ROUTING_KEY,
                    userId);
            throw new ManagerRegistrationException(ex.getMessage());
        }
    }

    public Manager getManagerInfo(Long managerId) throws GettingInfoException {
        try {
            Optional<Manager> optionalManager = managerRepository.findByUserId(managerId);

            if (optionalManager.isEmpty()) {
                throw new GettingInfoException(String.format("Manager with id %d doesn't exist", managerId));
            }

            return optionalManager.get();
        } catch (Throwable ex) {
            throw new GettingInfoException(String.format("Couldn't get manager with id %d", managerId));
        }
    }

    public Long findRestaurantByManagerEmail(String managerEmail){
       return managerRepository.findRestaurantIdByManagerEmail(managerEmail);
    }

}
