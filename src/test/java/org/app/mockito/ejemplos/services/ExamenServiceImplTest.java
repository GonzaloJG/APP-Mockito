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
class ExamenServiceImplTest {

    @Mock
    private ExamenRepositoryImpl repository;
    @Mock
    private PreguntaRepositoryImpl preguntaRepository;
    @InjectMocks
    private ExamenServiceImpl service;

    @Captor
    ArgumentCaptor<Long> captor;


    @BeforeEach
    void setUp() {
        //MockitoAnnotations.openMocks(this);
        /*repository = Mockito.mock(ExamenRepositoryOtro.class);
        preguntaRepository = Mockito.mock(PreguntaRepository.class);
        service = new ExamenServiceImpl(repository, preguntaRepository);*/

    }

    @Test
    void findExamenByName() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        Optional<Examen> examen = service.findExamenByName("Matematicas");

        assertTrue(examen.isPresent());
        assertEquals(5L, examen.orElseThrow().getId());
        assertEquals("Matematicas", examen.get().getNombre());
    }

    @Test
    void findExamenByNameEmptyList() {
        List<Examen> datos = Collections.emptyList();
        when(repository.findAll()).thenReturn(datos);

        Optional<Examen> examen = service.findExamenByName("Matematicas");

        assertFalse(examen.isPresent());
    }

    @Test
    void testPreguntaExamen() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");

        assertNotNull(examen);
        assertEquals(5, examen.getPreguntas().size());
    }

    @Test
    void testPreguntaExamenVerify() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");

        assertNotNull(examen);
        assertEquals(5, examen.getPreguntas().size());
        Mockito.verify(repository).findAll();
        Mockito.verify(preguntaRepository).findByExamenId(anyLong());
    }

    @Test
    void testNoExisteExamenVerify() {
        // GIVEN
        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(preguntaRepository.findByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        // WHEN
        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas2");
        // THEN
        assertNull(examen);
        Mockito.verify(repository).findAll();
        Mockito.verify(preguntaRepository).findByExamenId(5L);
    }

    @Test
    void guardarExmamen(){
        // GIVEN
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        when(repository.save(any(Examen.class))).then(new Answer<Examen>() {
            Long secuencia = 8L;
            @Override
            public Examen answer(InvocationOnMock invocation) throws Throwable {
                Examen examen = invocation.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        });

        // WHEN
        Examen examen = service.guardar(newExamen);

        // THEN
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());

        verify(repository).save(any(Examen.class));
        verify(preguntaRepository).save(anyList());
    }

    @Test
    void testManejoException(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
        when(preguntaRepository.findByExamenId(isNull())).thenThrow(IllegalArgumentException.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamenPorNombreConPreguntas("Matematicas");
    });
        assertEquals(IllegalArgumentException.class, exception.getClass());

        verify(repository).findAll();
        verify(preguntaRepository).findByExamenId(isNull());
    }

    @Test
    void testArgumentMatchers(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Lenguaje");

        verify(repository).findAll();
        //verify(preguntaRepository).findByExamenId(argThat(arg -> arg.equals(6L)));
        //verify(preguntaRepository).findByExamenId(eq(5L));
        verify(preguntaRepository).findByExamenId(argThat(arg -> arg != null && arg >= 6));

    }

    @Test
    void testArgumentMatchers2(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NEGATIVOS);
        when(preguntaRepository.findByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Lenguaje");

        verify(repository).findAll();
        verify(preguntaRepository).findByExamenId(argThat(new MisArgsMatchers()));

    }

    @Test
    void testArgumentMatchers3(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NEGATIVOS);
        when(preguntaRepository.findByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(repository).findAll();
        verify(preguntaRepository).findByExamenId(argThat( argument -> argument != null && argument > 0));

    }

    public static class MisArgsMatchers implements ArgumentMatcher<Long> {

        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return "es para un mensaje personalizado de error " +
                    "que imprime mockito en caso de falle el test: " +
                    + argument +" debe ser un entero positivo";
        }
    }

    @Test
    void testArgumentCaptor(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        //when(preguntaRepository.findByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

        //ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(preguntaRepository).findByExamenId(captor.capture());

        assertEquals(5L, captor.getValue());
    }

    @Test
    void testDoThrow(){
        Examen examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);
        doThrow(IllegalArgumentException.class).when(preguntaRepository).save(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
            service.guardar(examen);
        });
    }

    @Test
    void testDoAnswer(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        //when(preguntaRepository.findByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        doAnswer(invocation -> {
           Long id = invocation.getArgument(0);
           return id == 5L? Datos.PREGUNTAS : null;
        }).when(preguntaRepository).findByExamenId(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");

        assertNotNull(examen);
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("geometria"));
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());

        verify(preguntaRepository).findByExamenId(anyLong());
    }

    @Test
    void testGuardarExamen(){
        // GIVEN
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

        doAnswer(new Answer<Examen>() {
            Long secuencia = 8L;
            @Override
            public Examen answer(InvocationOnMock invocation) throws Throwable {
                Examen examen = invocation.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        }).when(repository).save(any(Examen.class));

        // WHEN
        Examen examen = service.guardar(newExamen);

        // THEN
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());

        verify(repository).save(any(Examen.class));
        verify(preguntaRepository).save(anyList());
    }

    @Test
    void testDoCallRealMethod() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
      //  when(preguntaRepository.findByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        doCallRealMethod().when(preguntaRepository).findByExamenId(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertNotNull(examen);
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
    }

    @Test
    void testSpy(){
        ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
        PreguntaRepository preguntaRepository = spy(PreguntaRepositoryImpl.class);
        ExamenService examenService = new ExamenServiceImpl(examenRepository, preguntaRepository);

        List<String> preguntas = Arrays.asList("aritmetica");
        //when(preguntaRepository.findByExamenId(anyLong())).thenReturn(preguntas);
        doReturn(preguntas).when(preguntaRepository).findByExamenId(anyLong());

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");

        assertNotNull(examen);
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
        assertEquals(1, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));

        verify(examenRepository).findAll();
        verify(preguntaRepository).findByExamenId(anyLong());
    }

    @Test
    void testOrdenDeInvocaciones(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matematicas");
        service.findExamenPorNombreConPreguntas("Historia");

        InOrder inOrder = inOrder(preguntaRepository);
        inOrder.verify(preguntaRepository).findByExamenId(5L);
        inOrder.verify(preguntaRepository).findByExamenId(6L);
    }

    @Test
    void testOrdenDeInvocaciones2(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matematicas");
        service.findExamenPorNombreConPreguntas("Historia");

        InOrder inOrder = inOrder(repository, preguntaRepository);
        inOrder.verify(repository).findAll();
        inOrder.verify(preguntaRepository).findByExamenId(5L);

        inOrder.verify(repository).findAll();
        inOrder.verify(preguntaRepository).findByExamenId(6L);
    }

    @Test
    void testNumeroDeInvocaciones(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(preguntaRepository, times(1)).findByExamenId(5L);
        verify(preguntaRepository, atLeast(1)).findByExamenId(5L);
        verify(preguntaRepository, atLeastOnce()).findByExamenId(5L);
        verify(preguntaRepository, atMost(1)).findByExamenId(5L);
        verify(preguntaRepository, atMostOnce()).findByExamenId(5L);
    }

    @Test
    void testNumeroDeInvocaciones2(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        service.findExamenPorNombreConPreguntas("Matematicas");

        //verify(preguntaRepository).findByExamenId(5L); falla
        verify(preguntaRepository, times(2)).findByExamenId(5L);
        verify(preguntaRepository, atLeast(2)).findByExamenId(5L);
        verify(preguntaRepository, atLeastOnce()).findByExamenId(5L);
        verify(preguntaRepository, atMost(20)).findByExamenId(5L);
        //verify(preguntaRepository, atMostOnce()).findByExamenId(5L); falla
    }

    @Test
    void testNumeroDeInvocaciones3(){
        when(repository.findAll()).thenReturn(Collections.emptyList());
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(preguntaRepository, never()).findByExamenId(5L);
        verifyNoInteractions(preguntaRepository);
        verify(repository).findAll();
        verify(repository, times(1)).findAll();
        verify(repository, atLeast(1)).findAll();
        verify(repository, atLeastOnce()).findAll();
        verify(repository, atMost(1)).findAll();
        verify(repository, atMostOnce()).findAll();
    }

}