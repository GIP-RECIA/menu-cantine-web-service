package fr.recia.menucantine.adoria;

import fr.recia.menucantine.adoria.beans.ReponseAdoria;
import fr.recia.menucantine.adoria.beans.RequeteAdoria;

public interface IRestAdoriaClient {

	ReponseAdoria call(RequeteAdoria requete) throws RestAdoriaClientException;

}
