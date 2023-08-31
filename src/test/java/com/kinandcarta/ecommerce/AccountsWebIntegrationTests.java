package com.kinandcarta.ecommerce;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AccountsController.class)
@Import(AccountsHandler.class)
class AccountsWebIntegrationTests {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	AccountsRepository accountsRepository;

	ObjectMapper mapper = new ObjectMapper();

	@BeforeEach void setUp() {
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.registerModule(new JavaTimeModule());
	}
	@Test void shouldCreateAnAccount_withAddress() throws Exception {
		final String json = mapper.writeValueAsString(Accounts.builder()
				.firstName("DukeFirstName")
				.lastName("DukeLastName")
				.emailAddress("dukefirstlast@duke.com").build());

		Accounts initial = Accounts.builder()
				.id(1L)
				.firstName("Updated Firstname")
				.lastName("Updated Lastname")
				.emailAddress("updatedfirstlast@duke.com").build();
		when(accountsRepository.save(initial)).thenReturn(initial);

		mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content((json)))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk());

	}
	@Test void shouldUpdateAnAccount() throws Exception {
		final String json = mapper.writeValueAsString(Accounts.builder()
				.id(20L)
				.firstName("DukeFirstName")
				.lastName("DukeLastName")
				.emailAddress("dukefirstlast@duke.com").build());

		when(accountsRepository.findById(20L)).thenReturn(
                Optional.ofNullable(Accounts.builder()
                        .id(20L)
                        .firstName("Updated Firstname")
                        .lastName("Updated Lastname")
                        .emailAddress("updatedfirstlast@duke.com").build()));

		mockMvc.perform(MockMvcRequestBuilders.put("/accounts/{id}", 20L)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content((json)))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
	@Test void shouldDeleteAnAccount() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/{id}", 1L))
				.andExpect(MockMvcResultMatchers.status().isAccepted());
	}
	@Test void shouldFindAnAccount_byId() throws Exception {
		when(accountsRepository.findById(1L)).thenReturn(
				Optional.ofNullable(Accounts.builder()
                .firstName("DukeFirstName")
                .lastName("DukeLastName")
                .emailAddress("dukefirstlast@duke.com").build()));

		mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{id}", 1L)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.firstName").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.lastName").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.emailAddress").exists());

	}
	@Test void shouldFind_AllAccounts() throws Exception {
		when(accountsRepository.findAll()).thenReturn(
				List.of(Accounts.builder()
				.firstName("DukeFirstName")
				.lastName("DukeLastName")
				.emailAddress("dukefirstlast@duke.com").build()));
		mockMvc.perform(MockMvcRequestBuilders.get("/accounts")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test void shouldFindAllAddresses_forAccountId() throws Exception {
		given(accountsRepository.findById(21L)).willReturn(
				Optional.ofNullable(Accounts.builder()
						.id(21L)
						.firstName("DukeFirstName")
						.lastName("DukeLastName")
						.emailAddress("dukefirstlast@duke.com")
						.addresses(
								Set.of(
										Address.builder()
											.id(100L)
											.address1("100 Noworries Avenue")
											.address2("Suite #100")
											.city("Food Forest City")
											.state("FL")
											.province("")
											.postalCode("33000")
											.country("US").build(),
										Address.builder()
											.id(105L)
											.address1("105 Ood Nutrients Drive")
											.address2("Suite #105")
											.city("Ood City")
											.state("FL")
											.province("")
											.postalCode("33000")
											.country("US").build()
								)
						)
						.build()));
		mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{id}/addresses", 21L)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.[*].id").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.[*].address1").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.[*].city").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.[*].state").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.[*].postalCode").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.[*].country").exists());

	}

}
