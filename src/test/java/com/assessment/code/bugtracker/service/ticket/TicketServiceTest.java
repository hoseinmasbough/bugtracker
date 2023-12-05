package com.assessment.code.bugtracker.service.ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.assessment.code.bugtracker.data.ticket.Ticket;
import com.assessment.code.bugtracker.data.ticket.TicketDao;
import com.assessment.code.bugtracker.data.user.User;
import com.assessment.code.bugtracker.data.user.UserDao;
import com.assessment.code.bugtracker.exception.BusinessValidationException;
import com.assessment.code.bugtracker.exception.TicketNotFoundException;
import com.assessment.code.bugtracker.service.ticket.dto.CreateTicketRequestDto;
import com.assessment.code.bugtracker.service.ticket.dto.TicketResponseDto;
import com.assessment.code.bugtracker.service.ticket.dto.UpdateTicketRequestDto;
import com.assessment.code.bugtracker.service.ticket.impl.TicketServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@Import({ TicketServiceImpl.class })
@ComponentScan(basePackages = { "com.assessment.code.bugtracker.service.ticket.mapper" })
class TicketServiceTest {

	@Autowired
	private TicketServiceImpl service;

	@MockBean
	private TicketDao ticketDao;

	@MockBean
	private UserDao userDao;

	@Test
	@DisplayName("save ticket - success")
	void save_success() throws BusinessValidationException {
		Long assigneeId = 1L;
		Long reporterId = 2L;
		CreateTicketRequestDto requestDto = createTicketRequestDto(reporterId, assigneeId);
		User reporter = createUser(reporterId);
		User assignee = createUser(assigneeId);
		Mockito.when(userDao.findById(requestDto.getReporter())).thenReturn(Optional.of(reporter));
		Mockito.when(userDao.findById(requestDto.getAssignee())).thenReturn(Optional.of(assignee));
		Mockito.when(ticketDao.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

		TicketResponseDto responseDto = service.save(requestDto);

		assertThat(responseDto).isNotNull(); assertThat(responseDto.getAssignee()).isEqualTo(assignee.getId());
		assertThat(responseDto.getReporter()).isEqualTo(reporter.getId());
		assertThat(responseDto.getName()).isEqualTo(requestDto.getName());
		assertThat(responseDto.getDescription()).isEqualTo(requestDto.getDescription());
		assertThat(responseDto.getAffectsVersions()).isEqualTo(requestDto.getAffectsVersions());
		assertThat(responseDto.getPriorityType()).isEqualTo(requestDto.getPriorityType());
		assertThat(responseDto.getStatus()).isEqualTo(requestDto.getStatus());
		assertThat(responseDto.getResolved()).isEqualTo(requestDto.getResolved());

		Mockito.verify(ticketDao, Mockito.times(1)).save(ArgumentMatchers.any(Ticket.class));
		Mockito.verify(userDao, Mockito.times(2)).findById(ArgumentMatchers.any(Long.class));
	}

	@Test
	@DisplayName("save ticket - assignee not found - failed")
	void save_failed_1() {
		Long assigneeId = 1L;
		Long reporterId = 2L;
		CreateTicketRequestDto requestDto = createTicketRequestDto(reporterId, assigneeId);
		User reporter = createUser(reporterId);
		Mockito.when(userDao.findById(requestDto.getReporter())).thenReturn(Optional.of(reporter));
		Mockito.when(userDao.findById(requestDto.getAssignee())).thenReturn(Optional.empty());

		BusinessValidationException exception = Assertions.assertThrows(BusinessValidationException.class, () -> service.save(requestDto));

		String expectedMessage = "user not found, userId: "; String actualMessage = exception.getMessage();
		Assertions.assertTrue(actualMessage.contains(expectedMessage));

		Mockito.verify(ticketDao, Mockito.never()).save(ArgumentMatchers.any(Ticket.class));
		Mockito.verify(userDao, Mockito.times(2)).findById(ArgumentMatchers.any(Long.class));
	}

	@Test
	@DisplayName("save ticket - reporter not found - failed")
	void save_failed_2() {
		Long assigneeId = 1L;
		Long reporterId = 2L;
		CreateTicketRequestDto requestDto = createTicketRequestDto(reporterId, assigneeId);
		User assignee = createUser(assigneeId);
		Mockito.when(userDao.findById(requestDto.getReporter())).thenReturn(Optional.empty());
		Mockito.when(userDao.findById(requestDto.getAssignee())).thenReturn(Optional.of(assignee));

		BusinessValidationException exception = Assertions.assertThrows(BusinessValidationException.class, () -> service.save(requestDto));

		String expectedMessage = "user not found, userId: "; String actualMessage = exception.getMessage();
		Assertions.assertTrue(actualMessage.contains(expectedMessage));

		Mockito.verify(ticketDao, Mockito.never()).save(ArgumentMatchers.any(Ticket.class));
		Mockito.verify(userDao, Mockito.times(1)).findById(ArgumentMatchers.any(Long.class));
	}

	@Test
	@DisplayName("delete ticket - success")
	void delete_success() throws TicketNotFoundException {
		Long ticketId = 1L;
		Long assigneeId = 1L;
		Ticket ticket = createTicket(ticketId, assigneeId);
		Mockito.when(ticketDao.findById(ticketId)).thenReturn(Optional.of(ticket));
		Mockito.doNothing().when(ticketDao).delete(any(Ticket.class));

		service.delete(ticketId);

		Mockito.verify(ticketDao, Mockito.times(1)).findById(ArgumentMatchers.any(Long.class));
		Mockito.verify(ticketDao, Mockito.times(1)).delete(ArgumentMatchers.any(Ticket.class));
	}

	@Test
	@DisplayName("delete ticket - no ticket found - failed")
	void delete_failed() {
		Long ticketId = 1L;
		Mockito.when(ticketDao.findById(ticketId)).thenReturn(Optional.empty());
		Mockito.doNothing().when(ticketDao).delete(any(Ticket.class));

		TicketNotFoundException exception = Assertions.assertThrows(TicketNotFoundException.class, () -> service.delete(ticketId));

		String expectedMessage = "no ticket for id:"; String actualMessage = exception.getMessage();
		Assertions.assertTrue(actualMessage.contains(expectedMessage));

		Mockito.verify(ticketDao, Mockito.times(1)).findById(ArgumentMatchers.any(Long.class));
		Mockito.verify(ticketDao, Mockito.never()).delete(ArgumentMatchers.any(Ticket.class));
	}

	@Test
	@DisplayName("findByAssignee - exist - success")
	void findByAssignee_success_1() {
		Long ticketId = 1L;
		Long assigneeId = 1L;
		Ticket ticket = createTicket(ticketId, assigneeId);
		List<Ticket> tickets = List.of(ticket);
		Mockito.when(ticketDao.findAllByAssignee(ticketId, Pageable.ofSize(1))).thenReturn(new PageImpl<>(tickets));

		Page<TicketResponseDto> ticketResponse = service.findByAssignee(assigneeId, Pageable.ofSize(1));

		assertTicketResponse(ticket, ticketResponse);

		Mockito.verify(ticketDao, Mockito.times(1)).findAllByAssignee(any(), any());
	}

	@Test
	@DisplayName("findByAssignee - empty - success")
	void findByAssignee_success_2() {
		Long ticketId = 1L;
		Long assigneeId = 1L;
		Mockito.when(ticketDao.findAllByAssignee(ticketId, Pageable.ofSize(1))).thenReturn(Page.empty());

		Page<TicketResponseDto> tickets = service.findByAssignee(assigneeId, Pageable.ofSize(1));

		assertThat(tickets).isNotNull(); assertThat(tickets.getTotalPages()).isEqualTo(1);
		assertThat(tickets.getSize()).isZero(); List<TicketResponseDto> ticketList = tickets.getContent();
		assertThat(ticketList).isEmpty();

		Mockito.verify(ticketDao, Mockito.times(1)).findAllByAssignee(any(), any());
	}

	@Test
	@DisplayName("findAll - exist - success")
	void findAll_success_1() {
		Long ticketId = 1L;
		Long assigneeId = 1L;
		Ticket ticket = createTicket(ticketId, assigneeId);
		List<Ticket> tickets = List.of(ticket);
		Mockito.when(ticketDao.findAll(Pageable.ofSize(1))).thenReturn(new PageImpl<>(tickets));

		Page<TicketResponseDto> ticketResponse = service.findAll(Pageable.ofSize(1));

		assertTicketResponse(ticket, ticketResponse);

		Mockito.verify(ticketDao, Mockito.times(1)).findAll(Pageable.ofSize(1));
	}

	@Test
	@DisplayName("findAll - empty - success")
	void findAll_success_2() {
		Mockito.when(ticketDao.findAll(Pageable.ofSize(1))).thenReturn(Page.empty());

		Page<TicketResponseDto> ticketResponse = service.findAll(Pageable.ofSize(1));

		assertThat(ticketResponse).isNotNull(); assertThat(ticketResponse.getTotalPages()).isEqualTo(1);
		assertThat(ticketResponse.getSize()).isZero(); List<TicketResponseDto> ticketList = ticketResponse.getContent();
		assertThat(ticketList).isEmpty();

		Mockito.verify(ticketDao, Mockito.times(1)).findAll(Pageable.ofSize(1));
	}

	@Test
	@DisplayName("update - success")
	void update_success_1() throws TicketNotFoundException, BusinessValidationException {
		Long ticketId = 1L;
		Long assigneeId = 1L;
		Ticket ticket = createTicket(ticketId, assigneeId);
		UpdateTicketRequestDto request = createUpdateTicketRequestDto(ticketId, null);
		Mockito.when(ticketDao.findById(ticketId)).thenReturn(Optional.of(ticket));
		Mockito.when(ticketDao.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

		service.update(request);

		Mockito.verify(ticketDao, Mockito.times(1)).save(ArgumentMatchers.any(Ticket.class));
		Mockito.verify(ticketDao, Mockito.times(1)).findById(any(Long.class));
		Mockito.verify(userDao, Mockito.never()).findById(any(Long.class));
	}

	@Test
	@DisplayName("update - with assignee - success")
	void update_success_2() throws TicketNotFoundException, BusinessValidationException {
		Long ticketId = 1L;
		Long assigneeId = 1L;
		Long updatedAssigneeId = 2L;
		Ticket ticket = createTicket(ticketId, assigneeId);
		UpdateTicketRequestDto request = createUpdateTicketRequestDto(ticketId, updatedAssigneeId);
		Mockito.when(ticketDao.findById(ticketId)).thenReturn(Optional.of(ticket));
		Mockito.when(userDao.findById(updatedAssigneeId)).thenReturn(Optional.of(createUser(updatedAssigneeId)));
		Mockito.when(ticketDao.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

		service.update(request);

		Mockito.verify(ticketDao, Mockito.times(1)).save(ArgumentMatchers.any(Ticket.class));
		Mockito.verify(ticketDao, Mockito.times(1)).findById(any(Long.class));
		Mockito.verify(userDao, Mockito.times(1)).findById(any(Long.class));
	}

	@Test
	@DisplayName("update - no ticket - failed")
	void update_failed_1() {
		Long ticketId = 1L;
		Long assigneeId = 1L;
		UpdateTicketRequestDto request = createUpdateTicketRequestDto(ticketId, assigneeId);
		Mockito.when(ticketDao.findById(ticketId)).thenReturn(Optional.empty());

		TicketNotFoundException exception = Assertions.assertThrows(TicketNotFoundException.class, () -> service.update(request));

		String expectedMessage = "no ticket for id:"; String actualMessage = exception.getMessage();
		Assertions.assertTrue(actualMessage.contains(expectedMessage));

		Mockito.verify(ticketDao, Mockito.never()).save(ArgumentMatchers.any(Ticket.class));
		Mockito.verify(ticketDao, Mockito.times(1)).findById(any(Long.class));
		Mockito.verify(userDao, Mockito.never()).findById(any(Long.class));
	}

	@Test
	@DisplayName("update - no user - failed")
	void update_failed_2() {
		Long ticketId = 1L;
		Long assigneeId = 1L;
		Ticket ticket = createTicket(ticketId, assigneeId);
		UpdateTicketRequestDto request = createUpdateTicketRequestDto(ticketId, 2L);
		Mockito.when(ticketDao.findById(ticketId)).thenReturn(Optional.empty());
		Mockito.when(userDao.findById(ticketId)).thenReturn(Optional.empty());
		Mockito.when(ticketDao.findById(ticketId)).thenReturn(Optional.of(ticket));

		BusinessValidationException exception = Assertions.assertThrows(BusinessValidationException.class, () -> service.update(request));

		String expectedMessage = "user not found, userId: ";
		String actualMessage = exception.getMessage();
		Assertions.assertTrue(actualMessage.contains(expectedMessage));

		Mockito.verify(ticketDao, Mockito.never()).save(ArgumentMatchers.any(Ticket.class));
		Mockito.verify(ticketDao, Mockito.times(1)).findById(any(Long.class));
		Mockito.verify(userDao, Mockito.times(1)).findById(any(Long.class));
	}

	private UpdateTicketRequestDto createUpdateTicketRequestDto(Long ticketId, Long assigneeId) {
		UpdateTicketRequestDto updateTicketRequestDto = new UpdateTicketRequestDto();
		updateTicketRequestDto.setId(ticketId);
		updateTicketRequestDto.setAssignee(assigneeId);
		return updateTicketRequestDto;
	}

	private void assertTicketResponse(Ticket ticket, Page<TicketResponseDto> ticketResponse) {
		assertThat(ticketResponse).isNotNull();
		assertThat(ticketResponse.getTotalPages()).isEqualTo(1);
		assertThat(ticketResponse.getSize()).isEqualTo(1);
		List<TicketResponseDto> ticketList = ticketResponse.getContent();
		assertThat(ticketList).hasSize(1);
		assertThat(ticketList.get(0).getResolved()).isEqualTo(ticket.getResolved());
		assertThat(ticketList.get(0).getName()).isEqualTo(ticket.getName());
		assertThat(ticketList.get(0).getAssignee()).isEqualTo(ticket.getAssignee().getId());
		assertThat(ticketList.get(0).getStatus()).isEqualTo(ticket.getStatus());
		assertThat(ticketList.get(0).getDescription()).isEqualTo(ticket.getDescription());
		assertThat(ticketList.get(0).getReporter()).isEqualTo(ticket.getReporter().getId());
		assertThat(ticketList.get(0).getPriorityType()).isEqualTo(ticket.getPriorityType());
		assertThat(ticketList.get(0).getAffectsVersions()).isEqualTo(ticket.getAffectsVersions());
		assertThat(ticketList.get(0).getFixVersions()).isEqualTo(ticket.getFixVersions());
		assertThat(ticketList.get(0).getId()).isEqualTo(ticket.getId());
		assertThat(ticketList.get(0).getSprint()).isEqualTo(ticket.getSprint());
		assertThat(ticketList.get(0).getResolved()).isEqualTo(ticket.getResolved());
	}

	private Ticket createTicket(Long ticketId, Long userId) {
		Ticket ticket = new Ticket();
		ticket.setId(ticketId);
		ticket.setAssignee(createUser(userId));
		ticket.setDescription("desc");
		ticket.setName("name");
		ticket.setAffectsVersions(List.of("v1"));
		ticket.setResolved(LocalDateTime.now());
		ticket.setStatus(1); ticket.setPriorityType(2);
		ticket.setFixVersions(List.of("v2"));
		ticket.setSprint(List.of("sp1"));
		ticket.setReporter(createUser(5L));
		return ticket;
	}

	private User createUser(Long userId) {
		User user = new User();
		user.setName("David");
		user.setId(userId);
		return user;
	}

	private CreateTicketRequestDto createTicketRequestDto(Long reporterId, Long assigneeId) {
		CreateTicketRequestDto dto = new CreateTicketRequestDto();
		dto.setAssignee(assigneeId);
		dto.setReporter(reporterId);
		dto.setName("bug fixed in live env");
		dto.setDescription("bug multiple fixed in live env reported by operation");
		dto.setAffectsVersions(List.of("1.2.0"));
		dto.setPriorityType(2); dto.setStatus(1);
		dto.setResolved(LocalDateTime.now());
		return dto;
	}
}
