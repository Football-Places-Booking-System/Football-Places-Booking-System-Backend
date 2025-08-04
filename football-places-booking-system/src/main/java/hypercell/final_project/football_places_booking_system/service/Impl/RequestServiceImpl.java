package hypercell.final_project.football_places_booking_system.service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.model.db.Request;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.RequestDTO;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import hypercell.final_project.football_places_booking_system.model.enums.RequestType;
import hypercell.final_project.football_places_booking_system.model.enums.ResponseStatus;
import hypercell.final_project.football_places_booking_system.repository.RequestRepository;
import hypercell.final_project.football_places_booking_system.repository.UserRepository;
import hypercell.final_project.football_places_booking_system.service.Interfaces.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService  {
    
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    
    @Override
    public Request createRequest(UUID senderId, UUID receiverId, RequestType requestType, UUID joker_id) throws AppException {
        log.info("Creating request from sender {} to receiver {} of type {}", senderId, receiverId, requestType);
        
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        
        Request request = Request.builder()
                .sender(sender)
                .receiver(receiver)
                .requestType(requestType)
                .status(ResponseStatus.PENDING)
                .sendTime(LocalDateTime.now())
                .jokerId(joker_id)
                .build();
        
        Request savedRequest = requestRepository.save(request);
        log.info("Successfully created request with ID: {}", savedRequest.getId());
        
        return savedRequest;
    }
    
    @Override
    public Request createRequestWithMessage(UUID senderId, UUID receiverId, RequestType requestType, String message, UUID joker_id) throws AppException {
        log.info("Creating request with message from sender {} to receiver {} of type {}", senderId, receiverId, requestType);
        
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        
        Request request = Request.builder()
                .sender(sender)
                .receiver(receiver)
                .requestType(requestType)
                .status(ResponseStatus.PENDING)
                .sendTime(LocalDateTime.now())
                .requestMessage(message)
                .jokerId(joker_id)
                .build();
        
        Request savedRequest = requestRepository.save(request);
        log.info("Successfully created request with ID: {} and message: {}", savedRequest.getId(), message);
        
        return savedRequest;
    }
    
    @Override
    public Request updateRequestStatus(UUID requestId, ResponseStatus status) throws AppException {
        log.info("Updating request {} status to {}", requestId, status);
        
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REQUEST_NOT_FOUND));
        
        request.setStatus(status);
        request.setResponseTime(LocalDateTime.now());
        
        Request updatedRequest = requestRepository.save(request);
        log.info("Successfully updated request {} status to {}", requestId, status);
        
        return updatedRequest;
    }
    
    @Override
    public Request updateRequestStatusWithMessage(UUID requestId, ResponseStatus status, String responseMessage) throws AppException {
        log.info("Updating request {} status to {} with message: {}", requestId, status, responseMessage);
        
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REQUEST_NOT_FOUND));
        
        request.setStatus(status);
        request.setResponseTime(LocalDateTime.now());
        
        // Set response message
        request.setResponseMessage(responseMessage);
        
        Request updatedRequest = requestRepository.save(request);
        log.info("Successfully updated request {} status to {} with response message", requestId, status);
        
        return updatedRequest;
    }
    
    @Override
    public Request getRequestById(UUID requestId) throws AppException{
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REQUEST_NOT_FOUND));
    }
    
    @Override
    public List<Request> getRequestsBySender(UUID senderId) throws AppException {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        return requestRepository.findBySender(sender);
    }
    
    @Override
    public List<RequestDTO> getRequestsByReceiver(UUID receiverId) throws AppException {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        // requestRepository.findByReceiver(receiver);
        return requestRepository.findByReceiver(receiver).stream()
                .map(request -> new RequestDTO(
                        request.getId(),
                        request.getSendTime(),
                        request.getRequestType(),
                        request.getStatus(),
                        request.getRequestMessage(),
                        request.getSender().getId(),
                        request.getReceiver().getId(),
                        request.getJokerId(),
                        request.getSender().getEmail()
                ))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Request> getPendingRequestsByReceiver(UUID receiverId) throws AppException {
        return requestRepository.findByReceiverIdAndStatus(receiverId, ResponseStatus.PENDING);
    }
    
    @Override
    public boolean hasExistingRequest(UUID senderId, UUID receiverId, RequestType requestType) throws AppException {
        return requestRepository.findBySenderIdAndReceiverIdAndRequestType(senderId, receiverId, requestType)
                .isPresent();
    }
}