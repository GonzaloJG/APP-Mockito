package org.app.mockito.ejemplos.services;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.app.mockito.ejemplos.models.Examen;
import org.app.mockito.ejemplos.repositories.ExamenRepository;
import org.app.mockito.ejemplos.repositories.PreguntaRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class ExamenServiceImpl implements ExamenService {

    private ExamenRepository examenRepository;
    private PreguntaRepository preguntaRepository;

    @Override
    public  Optional<Examen> findExamenByName(String name) {
        return examenRepository.findAll().stream()
                .filter(examen -> examen.getNombre().equals(name))
                .findFirst();
    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        Optional<Examen> examenOptional = findExamenByName(nombre);
        Examen examen = null;
        if(examenOptional.isPresent()){
            examen = examenOptional.get();
            List<String> preguntas = preguntaRepository.findByExamenId(examen.getId());
            preguntaRepository.findByExamenId(examen.getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }

    @Override
    public Examen guardar(Examen examen) {
        if(!examen.getPreguntas().isEmpty()){
            preguntaRepository.save(examen.getPreguntas());
        }
        return examenRepository.save(examen);
    }
}
