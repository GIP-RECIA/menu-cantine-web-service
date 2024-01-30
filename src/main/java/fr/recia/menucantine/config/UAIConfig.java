/**
 * Copyright (C) 2024 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2024 GIP-RECIA
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
 * Configuration des UAI mappés sur d'autres UAI
 * Charge depuis un fichier externalisé une map associant {uai_entrée : uai_sortie}
 * - L'UAI en entrée est celui qui est reçu depuis le front
 * - L'UAI en sortie est celui qui est utilisé pour interroger l'API
 */
@Component
@ConfigurationProperties(prefix = "uais")
@Getter
@Setter
public class UAIConfig {

    private Map<String, String> regroupements;

    public boolean isMapped(String uai){
        return regroupements.containsKey(uai);
    }

    public String getNewUAI(String uai){
        return regroupements.get(uai);
    }

}