/**
 * Copyright (C) 2023 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2023 Nathan Cailbourdin <nathan.cailbourdin@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.menucantine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Configuration du Mapper
 * Charge depuis un fichier externalisé :
 * - Les différents services avec leur numéro et leur nom
 * - Les différents sous-menus avec leur numéro, leur nom dans l'API et leur nom à afficher
 */
@Component
@ConfigurationProperties(prefix = "mapper")
@Getter
@Setter
public class MapperConfig {

    private Map<String, Integer> services;
    private Map<String, SousMenuProperties> sousmenus;

    public String serviceKeyFromValue(int numservice){
        for(String nomservice : services.keySet()){
            if(services.get(nomservice) == numservice){
                return nomservice;
            }
        }
        return null;
    }

    public int getNumService(String nomService){
        return this.services.get(nomService);
    }

    public int getSousMenuRank(String nomSousMenu){
        return this.sousmenus.get(nomSousMenu).getNumero();
    }

    public String getSousMenuFinalName(String nomSousMenu){
        return this.sousmenus.get(nomSousMenu).getNom();
    }

}