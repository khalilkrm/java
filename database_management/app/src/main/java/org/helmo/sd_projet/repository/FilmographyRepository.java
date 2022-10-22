package org.helmo.sd_projet.repository;

import org.helmo.sd_projet.domain.Movie;
import org.helmo.sd_projet.repository.exceptions.ExportException;

public interface FilmographyRepository {
    JSONFilmographyRepository.ExportResult export(final String filename, final Movie movie) throws ExportException;

    JSONFilmographyRepository.ImportResult importFile(final String filename);
}
