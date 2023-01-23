package co.com.sofkau.entrenamento.curso;


import co.com.sofka.business.generic.UseCaseHandler;
import co.com.sofka.business.repository.DomainEventRepository;
import co.com.sofka.business.support.RequestCommand;
import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofkau.entrenamiento.curso.commands.AgregarDirectrizMentoria;
import co.com.sofkau.entrenamiento.curso.events.CursoCreado;
import co.com.sofkau.entrenamiento.curso.events.DirectrizAgregadaAMentoria;
import co.com.sofkau.entrenamiento.curso.events.MentoriaCreada;
import co.com.sofkau.entrenamiento.curso.values.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AgregarDirectrizMentoriaUseCaseTest {

    @InjectMocks
    private AgregarDirectrizMentoriaUseCase useCase;
    @Mock
    private DomainEventRepository repository;

    @Test
    void agregarDirectrizAMentoria() {
        // ARRANGE
        CursoId cursoId = CursoId.of("xxxxx");
        MentoriaId mentoriaId = new MentoriaId("Mentoria");

        var command = new AgregarDirectrizMentoria(cursoId, mentoriaId, new Directiz("Directriz creada para prueba"));

        when(repository.getEventsBy("xxxxx")).thenReturn(history());
        useCase.addRepository(repository);

        // ACT
        var events = UseCaseHandler.getInstance()
                .setIdentifyExecutor(command.getCursoId().value())
                .syncExecutor(useCase, new RequestCommand<>(command))
                .orElseThrow()
                .getDomainEvents();

        // ASSERT
        var event = (DirectrizAgregadaAMentoria) events.get(0);
        Assertions.assertEquals("Directriz creada para prueba", event.getDirectiz().value());

    }

    private List<DomainEvent> history() {
        CursoId cursoId = new CursoId("xxxxx");
        Nombre nombre = new Nombre("Jose");
        Descripcion descripcion = new Descripcion("Practica");

        var eventoDeCurso = new CursoCreado(nombre, descripcion);
        eventoDeCurso.setAggregateRootId("xxxxx");

        var eventoDeMentoria = new MentoriaCreada(
                new MentoriaId("Mentoria"),
                new Nombre("Jose"),
                new Fecha(LocalDateTime.now(), LocalDate.now()));

        eventoDeMentoria.setAggregateRootId("Mentoria");

        return List.of(eventoDeCurso, eventoDeMentoria);
    }
}

