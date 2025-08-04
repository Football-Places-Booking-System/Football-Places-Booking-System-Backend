package hypercell.final_project.football_places_booking_system.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hypercell.final_project.football_places_booking_system.model.db.Request;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.enums.RequestType;
import hypercell.final_project.football_places_booking_system.model.enums.ResponseStatus;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {
    
    List<Request> findBySender(User sender);
    
    List<Request> findByReceiver(User receiver);
    
    List<Request> findByRequestType(RequestType requestType);
    
    List<Request> findByStatus(ResponseStatus status);

    Request findByJokerId(UUID jokerId);
    
    @Query("SELECT r FROM Request r WHERE r.sender.id = :senderId AND r.receiver.id = :receiverId AND r.requestType = :requestType")
    Optional<Request> findBySenderIdAndReceiverIdAndRequestType(
        @Param("senderId") UUID senderId, 
        @Param("receiverId") UUID receiverId, 
        @Param("requestType") RequestType requestType
    );
    
    @Query("SELECT r FROM Request r WHERE r.receiver.id = :receiverId AND r.status = :status")
    List<Request> findByReceiverIdAndStatus(@Param("receiverId") UUID receiverId, @Param("status") ResponseStatus status);
}