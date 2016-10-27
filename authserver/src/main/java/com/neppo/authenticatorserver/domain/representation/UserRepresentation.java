package com.neppo.authenticatorserver.domain.representation;

import java.util.Date;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.neppo.authenticatorserver.domain.User;


@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRepresentation extends ResourceSupport {

	
	@JsonInclude(Include.NON_NULL)
	private Long identifier;

	@JsonInclude(Include.NON_NULL)
	private String firstName;

	@JsonInclude(Include.NON_NULL)
	private String middleName;

	@JsonInclude(Include.NON_NULL)
	private String lastName;

	@JsonInclude(Include.NON_NULL)
	private String status;

	@JsonInclude(Include.NON_NULL)
	private String email;

	@JsonInclude(Include.NON_NULL)
	private String displayName;

	@JsonInclude(Include.NON_NULL)
	private Date effectiveDate;

	@JsonInclude(Include.NON_NULL)
	private String personalEmail;

	@JsonInclude(Include.NON_NULL)
	private Date birthDate;

	@JsonInclude(Include.NON_NULL)
	private String photo;

	@JsonInclude(Include.NON_NULL)
	private String phone;

	@JsonInclude(Include.NON_NULL)
	private String ramal;

	@JsonInclude(Include.NON_NULL)
	private AccountRepresentation masterAccount;

	@JsonInclude(Include.NON_NULL)
	private String justification;
	
	@JsonInclude(Include.NON_NULL)
	private String otpSecret;

	public UserRepresentation() {
	}

	public UserRepresentation(Long userId) {
		this.identifier = userId;
	}

	public UserRepresentation(User user) {
		this(user, false);
	}

	public UserRepresentation(User user, Boolean expand) {

		this.identifier = user.getId();
		this.firstName = user.getFirstName();
		this.middleName = user.getMiddleName();
		this.lastName = user.getLastName();
		this.status = user.getStatus().toString();
		this.email = user.getEmail();
		this.displayName = user.getDisplayName();
		this.effectiveDate = user.getEffectiveDate();
		this.personalEmail = user.getPersonalEmail();
		this.birthDate = user.getBirthDate();
		this.photo = user.getPhoto();
		this.phone = user.getPhone();
		this.ramal = user.getRamal();
		this.justification = user.getJustification();
		this.otpSecret = user.getOtpSecret();
		
		if(user.getMasterAccount() != null) {
			this.masterAccount = new AccountRepresentation(user.getMasterAccount());
		}
	}

	public static User build(UserRepresentation representation) {

		User user = new User();
		user.setId(representation.getIdentifier());
		user.setFirstName(representation.getFirstName());
		user.setMiddleName(representation.getMiddleName());
		user.setLastName(representation.getLastName());
		user.setStatus(representation.getStatus());
		user.setEmail(representation.getEmail());
		user.setDisplayName(representation.getDisplayName());
		user.setEffectiveDate(representation.getEffectiveDate());
		user.setPersonalEmail(representation.getPersonalEmail());
		user.setBirthDate(representation.getBirthDate());
		user.setPhoto(representation.getPhoto());
		user.setPhone(representation.getPhone());
		user.setRamal(representation.getRamal());
		user.setJustification(representation.getJustification());
		user.setOtpSecret(representation.getOtpSecret());
		
		if(representation.getMasterAccount() != null) {
			user.setMasterAccount(AccountRepresentation.build(representation.getMasterAccount()));
		}

		return user;
	}

	public Long getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getOtpSecret() {
		return otpSecret;
	}

	public void setOtpSecret(String otpSecret) {
		this.otpSecret = otpSecret;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getPersonalEmail() {
		return personalEmail;
	}

	public void setPersonalEmail(String personalEmail) {
		this.personalEmail = personalEmail;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRamal() {
		return ramal;
	}

	public void setRamal(String ramal) {
		this.ramal = ramal;
	}

	public AccountRepresentation getMasterAccount() {
		return masterAccount;
	}

	public void setMasterAccount(AccountRepresentation masterAccount) {
		this.masterAccount = masterAccount;
	}

	public String getJustification() {
		return justification;
	}

	public void setJustification(String justification) {
		this.justification = justification;
	}

}