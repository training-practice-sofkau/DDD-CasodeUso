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
        //arrange
        CursoId coursoId = CursoId.of("12345");
        MentoriaId mentoriaId = MentoriaId.of("AAAA");
        Directiz directiz = new Directiz("Actualizar");
        var command = new AgregarDirectrizMentoria(coursoId, mentoriaId, directiz);

        when(repository.getEventsBy("12345")).thenReturn(history());
        useCase.addRepository(repository);

        //act

        var events = UseCaseHandler.getInstance()
                .setIdentifyExecutor(command.getCursoId().value())
                .syncExecutor(useCase, new RequestCommand<>(command))
                .orElseThrow()
                .getDomainEvents();

        //assert

        var event = (DirectrizAgregadaAMentoria)events.get(0);
        Assertions.assertEquals("Actualizar", event.getDirectiz().value());

    }
    private List<DomainEvent> history() {
        //Curso
        CursoId cursoId = CursoId.of("12345");
        Nombre nombreC = new Nombre("Curso nuevo");
        Descripcion descripcion = new Descripcion("Este es un curso nuevo");
        //Mentoria
        MentoriaId mentoriaId = MentoriaId.of("AAAA");
        Nombre nombreM = new Nombre("Nueva mentoria");
        Fecha fecha = new Fecha(LocalDateTime.now(), LocalDate.now());

        var cursoEvent = new CursoCreado(nombreM, descripcion);
        cursoEvent.setAggregateRootId(cursoId.value());

        var mentoriaEvent = new MentoriaCreada(mentoriaId, nombreM, fecha);
        mentoriaEvent.setAggregateRootId(mentoriaId.value());

        return List.of(cursoEvent, mentoriaEvent);

    }
}

