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
        var command = new AgregarDirectrizMentoria(
                CursoId.of("10"),
                MentoriaId.of("1"),
                new Directiz("todos los días se llega puntual."));

        when(repository.getEventsBy("10")).thenReturn(events());
        useCase.addRepository(repository);

        //act
        var response = UseCaseHandler.getInstance()
                .setIdentifyExecutor("1")
                .syncExecutor(useCase, new RequestCommand<>(command))
                .orElseThrow();

        //assert
        var event = (DirectrizAgregadaAMentoria)response.getDomainEvents().get(0);
        Assertions.assertEquals("1",event.getMentoriaId().value());
        Assertions.assertEquals("todos los días se llega puntual.",event.getDirectiz().value());
        Assertions.assertEquals("co.com.sofkau.entrenamiento.DirectrizAgregadaAMentoria",event.type);

    }

    private List<DomainEvent> events() {
        CursoId cursoId = CursoId.of("10");
        Nombre nombreCurso = new Nombre("aerodinamicatech");
        Descripcion descripcion = new Descripcion("lectiva para el training");

       var  event = new CursoCreado(
               nombreCurso,
               descripcion
       );
       event.setAggregateRootId(cursoId.value());
       MentoriaId mentoriaId = MentoriaId.of("1");
       Nombre nombreMentoria = new Nombre("volar es facil");
       Fecha fecha = new Fecha(LocalDateTime.now(), LocalDate.now());

       var event2 = new MentoriaCreada(
               mentoriaId,
               nombreMentoria,
               fecha
       );
       event2.setAggregateRootId(mentoriaId.value());
       return List.of(event, event2);
    }
}

