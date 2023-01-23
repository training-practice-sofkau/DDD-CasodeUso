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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AgregarDirectrizMentoriaUseCaseTest {

    @InjectMocks
    private AgregarDirectrizMentoriaUseCase useCase;
    @Mock
    private DomainEventRepository repository;

    @Test
    void agregarDirectrizAMentoria() {

        CursoId cursoId = CursoId.of("idCurso");
        MentoriaId mentoriaId = MentoriaId.of("idMentoria");
        Directiz directiz = new Directiz("mate");



        var command = new AgregarDirectrizMentoria(cursoId, mentoriaId, directiz);

        when(repository.getEventsBy("idCurso")).thenReturn(cursos());
        useCase.addRepository(repository);

        var events = UseCaseHandler.getInstance()
                .setIdentifyExecutor(command.getCursoId().value())
                .syncExecutor(useCase, new RequestCommand<>(command))
                .orElseThrow()
                .getDomainEvents();

        var event = (DirectrizAgregadaAMentoria)events.get(0);
        assertEquals("mate", event.getDirectiz().value());



    }

    private List<DomainEvent> cursos(){
        CursoId cursoId = CursoId.of("idCurso");
        Nombre nombre = new Nombre("Mate");
        Descripcion descripcion = new Descripcion("Intensivo");

        MentoriaId mentoriaId = MentoriaId.of("idMentoria");
        Fecha fecha = new Fecha(LocalDateTime.now(),LocalDate.now());
        Nombre nombre2 = new Nombre("Mentoria");


        var event = new CursoCreado(nombre, descripcion);


        event.setAggregateRootId(cursoId.value());


        var event2 = new MentoriaCreada(mentoriaId,nombre2, fecha);

        event2.setAggregateRootId(cursoId.value());

        return List.of(event,event2);
    }
}

