package hypercell.final_project.football_places_booking_system.service.Interfaces;

import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.db.Request;
import hypercell.final_project.football_places_booking_system.model.enums.RequestType;
import hypercell.final_project.football_places_booking_system.model.enums.ResponseStatus;

public interface RequestService {
    
    Request createRequest(UUID senderId, UUID receiverId, RequestType requestType) throws AppException;
    
    Request createRequestWithMessage(UUID senderId, UUID receiverId, RequestType requestType, String message) throws AppException;
    
    Request updateRequestStatus(UUID requestId, ResponseStatus status) throws AppException;
    
    Request updateRequestStatusWithMessage(UUID requestId, ResponseStatus status, String responseMessage) throws AppException;
    
    Request getRequestById(UUID requestId) throws AppException;
    
    List<Request> getRequestsBySender(UUID senderId) throws AppException;
    
    List<Request> getRequestsByReceiver(UUID receiverId) throws AppException;
    
    List<Request> getPendingRequestsByReceiver(UUID receiverId) throws AppException;
    
    boolean hasExistingRequest(UUID senderId, UUID receiverId, RequestType requestType) throws AppException;
}