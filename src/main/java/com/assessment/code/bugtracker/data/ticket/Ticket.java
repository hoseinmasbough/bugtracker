package com.assessment.code.bugtracker.data.ticket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.assessment.code.bugtracker.data.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.stereotype.Indexed;

@Setter
@Getter
@ToString
@Entity
@Table(name = "tickets")
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String name;

	@ElementCollection
	private List<String> affectsVersions = new ArrayList<>();

	@ElementCollection
	private List<String> fixVersions = new ArrayList<>();

	@ElementCollection
	private List<String> sprint = new ArrayList<>();

	@Column
	private String description;

	@Column(nullable = false)
	private Integer status;

	@Column(nullable = false)
	private Integer priorityType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter_id")
	private User reporter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assignee_id")
	private User assignee;

	@Setter(AccessLevel.PRIVATE)
	@CreatedDate
	@Column(name = "created_date", columnDefinition = "TIMESTAMP")
	private LocalDateTime created = LocalDateTime.now();

	@Column(name = "resolved_date" , columnDefinition = "TIMESTAMP")
	private LocalDateTime resolved;
}
