package org.app.mockito.ejemplos.services;

import org.app.mockito.ejemplos.Datos;
import org.app.mockito.ejemplos.models.Examen;
import org.app.mockito.ejemplos.repositories.ExamenRepository;
import org.app.mockito.ejemplos.repositories.ExamenRepositoryImpl;
import org.app.mockito.ejemplos.repositories.PreguntaRepository;
import org.app.mockito.ejemplos.repositories.PreguntaRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplSpyTest {

    @Spy
    private ExamenRepositoryImpl repository;
    @Spy
    private PreguntaRepositoryImpl preguntaRepository;
    @InjectMocks
    private ExamenServiceImpl service;

    @Test
    void testSpy(){
        List<String> preguntas = Arrays.asList("aritmetica");
        //when(preguntaRepository.findByExamenId(anyLong())).thenReturn(preguntas);
        doReturn(preguntas).when(preguntaRepository).findByExamenId(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");

        assertNotNull(examen);
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
        assertEquals(1, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));

        verify(repository).findAll();
        verify(preguntaRepository).findByExamenId(anyLong());
    }
}