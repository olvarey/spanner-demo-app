package com.olvarey.spanner;

import static org.assertj.core.api.Assertions.assertThat;

import com.olvarey.spanner.note.CreateNoteRequest;
import com.olvarey.spanner.note.NoteResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class SpannerDemoAppApplicationTests {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void createAndFetchNotes() {
    CreateNoteRequest request = new CreateNoteRequest("First note", "Hello from tests");
    ResponseEntity<NoteResponse> createResponse = restTemplate.postForEntity("/notes", request,
        NoteResponse.class);

    assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    NoteResponse created = createResponse.getBody();
    assertThat(created).isNotNull();
    assertThat(created.id()).isNotNull();
    assertThat(created.title()).isEqualTo("First note");
    assertThat(created.body()).isEqualTo("Hello from tests");

    ResponseEntity<List<NoteResponse>> listResponse = restTemplate.exchange("/notes",
        HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<>() {
        });

    assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    List<NoteResponse> notes = listResponse.getBody();
    assertThat(notes).isNotNull();
    assertThat(notes).extracting(NoteResponse::id).contains(created.id());
  }

  @Test
  void deleteMissingNoteReturnsNotFound() {
    ResponseEntity<Void> response = restTemplate.exchange("/notes/999999", HttpMethod.DELETE,
        HttpEntity.EMPTY, Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
