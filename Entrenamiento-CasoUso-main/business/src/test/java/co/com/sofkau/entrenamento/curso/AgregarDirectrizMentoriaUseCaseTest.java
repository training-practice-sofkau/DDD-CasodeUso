package co.com.sofkau.entrenamento.curso;


import co.com.sofka.business.generic.UseCaseHandler;
import co.com.sofka.business.repository.DomainEventRepository;
import co.com.sofka.business.support.RequestCommand;
import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofkau.entrenamiento.curso.Curso;
import co.com.sofkau.entrenamiento.curso.commands.AgregarDirectrizMentoria;
import co.com.sofkau.entrenamiento.curso.events.CursoCreado;
import co.com.sofkau.entrenamiento.curso.events.DirectrizAgregadaAMentoria;
import co.com.sofkau.entrenamiento.curso.events.MentoriaCreada;
import co.com.sofkau.entrenamiento.curso.values.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AgregarDirectrizMentoriaUseCaseTest {


    @InjectMocks
    private AgregarDirectrizMentoriaUseCase useCase;

    @Mock
    private DomainEventRepository repository;

    @Test
    void agregarDirectrizHappyPass(){
        //arrange
        CursoId cursoId = CursoId.of("ddddd");
        MentoriaId mentoriaId = MentoriaId.of("ddddd");
        Directiz directiz = new Directiz("nnn");

        var command = new AgregarDirectrizMentoria( cursoId,mentoriaId,directiz);

        when(repository.getEventsBy("ddddd")).thenReturn(history());
        useCase.addRepository(repository);
        //act

        var events = UseCaseHandler.getInstance()
                .setIdentifyExecutor(command.getCursoId().value())
                .syncExecutor(useCase, new RequestCommand<>(command))
                .orElseThrow()
                .getDomainEvents();

        //assert
        var eventCurso = (DirectrizAgregadaAMentoria) events.get(0);
        assertEquals("nnn", eventCurso.getDirectiz().value());

    }

    private List<DomainEvent> history() {
        CursoId cursoId = CursoId.of("ddddd");
        //Nombre Curso
        Nombre nombrec = new Nombre("DDD");
        //Descripcion curso
        Descripcion descripcion = new Descripcion("Curso complementario para el training");
        MentoriaId mentoriaId = MentoriaId.of("ddddd");
        Nombre nombre = new Nombre("Aprendiendo de casos de usos");
        Fecha fecha = new Fecha(LocalDateTime.now(), LocalDate.now());
        var eventCurso = new CursoCreado(
                nombrec,
                descripcion
        );
        eventCurso.setAggregateRootId(cursoId.value());
        var eventMentoria = new MentoriaCreada(
                mentoriaId,
                nombre,
                fecha
        );
        eventMentoria.setAggregateRootId("ddddd");
        return List.of(eventCurso,eventMentoria);
    }
}

