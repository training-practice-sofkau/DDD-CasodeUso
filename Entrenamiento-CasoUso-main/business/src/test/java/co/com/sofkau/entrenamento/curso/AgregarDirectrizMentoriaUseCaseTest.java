package co.com.sofkau.entrenamento.curso;


import co.com.sofka.business.generic.UseCaseHandler;
import co.com.sofka.business.repository.DomainEventRepository;
import co.com.sofka.business.support.RequestCommand;
import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofkau.entrenamiento.curso.Mentoria;
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
        CursoId cursoId =CursoId.of("DDD");
        MentoriaId mentoriaId=new MentoriaId("mentoria");
        Directiz directiz = new Directiz("validacions");
        var command = new AgregarDirectrizMentoria(cursoId,mentoriaId,directiz);


        when(repository.getEventsBy("DDD")).thenReturn(history());
        useCase.addRepository(repository);

        var events = UseCaseHandler.getInstance()
                .setIdentifyExecutor(command.getCursoId().value())
                .syncExecutor(useCase, new RequestCommand<>(command))
                .orElseThrow()
                .getDomainEvents();

        //assert
        var event = (DirectrizAgregadaAMentoria)events.get(0);
        Assertions.assertEquals("validacions", event.getDirectiz().value());


    }
    private List<DomainEvent> history() {
        CursoId cursoId=new CursoId("curso");
        Nombre nombre = new Nombre("DDDs");
        Descripcion descripcion = new Descripcion("Curso complementario para el training");
        var Cursoevent = new CursoCreado(
                nombre,
                descripcion
        );
        MentoriaId mentoriaId = MentoriaId.of("mentoria");
        Nombre nombreM = new Nombre("julian");
        Fecha fecha = new Fecha(LocalDateTime.now(), LocalDate.now());
        var mentoriaEvent = new MentoriaCreada(mentoriaId,nombreM,fecha);


        mentoriaEvent.setAggregateRootId(mentoriaId.value());
        Cursoevent.setAggregateRootId("DDD");



        return List.of(Cursoevent,mentoriaEvent);
    }
}

