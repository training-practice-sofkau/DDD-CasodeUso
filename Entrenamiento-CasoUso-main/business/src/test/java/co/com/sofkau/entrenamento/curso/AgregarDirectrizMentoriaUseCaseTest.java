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
        var command = new AgregarDirectrizMentoria(
                CursoId.of("11111"),
                MentoriaId.of("AAAAA"),
                new Directiz("Nueva directriz")
        );

        when(repository.getEventsBy("11111")).thenReturn(history());
        useCase.addRepository(repository);

        var events = UseCaseHandler.getInstance()
                .setIdentifyExecutor(command.getCursoId().value())
                .syncExecutor(useCase, new RequestCommand<>(command))
                .orElseThrow()
                .getDomainEvents();

        var event = (DirectrizAgregadaAMentoria)events.get(0);
        Assertions.assertEquals("Nueva directriz", event.getDirectiz().value());

    }
    private List<DomainEvent> history() {
        //Curso
        CursoId cursoId = CursoId.of("11111");
        Nombre nombreC = new Nombre("Nuevo curso");
        Descripcion descripcion = new Descripcion("Nuevo curso");
        //Mentoria
        MentoriaId mentoriaId = MentoriaId.of("AAAAA");
        Nombre nombreM = new Nombre("Nueva mentoria");
        Fecha fecha = new Fecha(LocalDateTime.now(), LocalDate.now());

        var cursoEvent = new CursoCreado(nombreM, descripcion);
        cursoEvent.setAggregateRootId(cursoId.value());

        var mentoriaEvent = new MentoriaCreada(mentoriaId, nombreM, fecha);
        mentoriaEvent.setAggregateRootId(mentoriaId.value());

        return List.of(cursoEvent, mentoriaEvent);
    }
}

