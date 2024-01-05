# Menu Cantine Web Service

Ce projet spring boot est la partie **back-end** du service Menu Cantine. Les technologies principalement utilisées ainsi que leurs versions sont données dans la liste suivante :
- spring-boot 2.7.17
- spring-security 5.7.11
- junit 4.13.2
- ehcache 2.10.9.2

# Architecture

## Structure générale

Les différents fichiers du projet sont structurés de la manière suivante :
```
.
├── src                                  # Contient les sources java et les fichiers de ressources
│   ├── main                
│   │   ├── java                       
│   │   │   ├── fr.recia.menucatine      # Contient le code métier
│   │   │   └── META-INF
│   │   ├── resources                    # Contient les fichiers de config par défaut et de ressources
│   │   │   ├── application.yml
│   │   │   ├── application-test.yml
│   │   │   ├── logback.xml
│   │   │   ├── ehcache.xml
│   │   │   ├── demo                    # Contient une version du front déjà compilée pour faire des tests en local
│   │   │   │   ├── demo.html
│   │   │   │   ├── menu-cantine.js
│   │   │   │   └── ...
│   │   │   └── ...
│   └── test                            # Contient les fichiers java pour les tests unitaires
│       └── ...            
├── target                              # Contient les classes compilées
├── pom.xml                             # Pom du projet
└── README.md
└── ...
```

## Code métier

Le package `fr.recia.menucatine` est lui structuré de la manière suivante :

```
.
├── adoria                                
│   ├── beans                                # Contient les objets de l'ancien back envoyés au front
│   │   ├── Journee.java                      
│   │   ├── Plat.java
│   │   ├── Service.java  
│   │   └── ...
│   └── RestClientCertConfiguration.java     # Configuration relative au https
│
├── beans
│   ├── Requete.java                         # Objet requete envoyé depuis le front
│   ├── RequeteHelper.java                   # Classe utilitaire, utilisée pour les convertions de dates par exemple
│   └── Semaine.java                         # Objet semaine envoyé vers le front, consituté de adoria.beans
│  
├── config                                   # Définition des services et sous-menus  
│   └── ...
│
├── dto                                      # Classes mappées depuis le JSON récupéré de l'API
│   ├── JourneeDTO.java                      # Objet construit à partir des deux classes ci-dessous
│   ├── PlatDTO.java                         # Représente un plat selon l'API
│   └── ServiceDTO.java                      # Représente un service selon l'API
│
├── exception                                # Les différentes exceptions personnalisées
│   └── ...
│
├── mapper                                   # Permet de transformer les objets de la nouvelle API en objets de l'ancienne API
│   ├── IMapper.java
│   └── MapperWebGerest.java
│
├── webgerest                                
│   ├── APIClient.java                       # Classe permettant de communiquer avec l'API
│   ├── AuthResponse.java                    # Objet stockant une réponse à une demande d'authentification
│   ├── CacheKeyRequete.java                 # Objet servant de clé pour le cache des requêtes
│   └── DynamicURLResponse.java              # Objet stockant une réponse à une demande d'url associée à un UAI
│
├── MenuCantineApplication.java              # Classe principale permettant de lancer l'application
├── MenuCantineController.java               # Controlleur de l'application
├── MenuCantineServices.java                 # Service appelé par le controlleur lors d'une requête
├── SecurityConfiguration.java               # Configuration de spring-security
└── ...
```

## Ancien back

Le package `adoria.beans` définit les différents objets qui vont constituer une `Semaine`, qui sera l'objet final envoyé au front. Ces classes servent aussi à **effectuer certains calculs préalables** sur ces objets, tels que la suppression de services vides, la complétion avec des plats vides pour alignement, ect... Ces calculs sont gérés ici et non pas dans le service ou dans les DTO afin de bien séparer les différents composants. Ce sont notamment les méthodes `clean()` des différents objets ainsi que les classes `NbPlatParSsMenu` et `NbPlatParSsMenuParService` qui effectuent ces calculs.

## Cheminement  d'une requête

Le **controlleur** commence par récupérer la requête faite depuis l'application. Celle-ci contient dans son `body` un objet `Requete` qui contient notamment la date et l'UAI du menu demandé. Le controlleur appelle alors le **service** pour qu'il construise un objet `Semaine`. Pour constuire cet objet, le service va faire appel à l'API.

Afin de faire une requête à l'API, plusieurs **opérations préalables** sont nécéssaires :
- Récupérer **l'URL** sur laquelle on va faire la requête. Pour cela on doit faire une première requête sur une URL particulière avec l'UAI de l'établissement donc on veut récupérer le menu. Un **dictionnaire** associe les UAI déjà demandés à leur URL afin de ne pas avoir à refaire une requête à chaque fois.
- Récupérer le **token** pour s'authentifier lorsqu'on effectue la requête. Ce token est demandé sur une l'URL récupérée ci-dessus avec un `client_secret` et un `client_id`. De la même manière, un **dictionnaire** associe les URL déjà demandés à leur token afin de ne pas avoir à refaire une requête à chaque fois.

Une fois ces deux éléments récupérés, on peut faire la requête à l'API. Pour cela on effectue un appel sur l'URL associée avec l'UAI avec le token dans le champ `Authorization`, et comme paramètres la `date`, `l'UAI` et le `numéro de service`. Pour récupérer une semaine entière, on doit donc faire **20 requêtes** (4 services pour 5 jours).

Le service regroupe tous les résultats de ces requêtes dans une `List<JourneeDTO>`, et fait appel au **mapper** qui va transformer les objets récupérés depuis l'API en objets de l'ancienne API, afin de pouvoir les envoyer au front. Le service "nettoie" ensuite et complète l'objet `Semaine` renvoyé par le mapper en faisant appel aux méthodes `clean` et `complete` de la classe `Semaine`. Ensuite, le service peut retourner la `Semaine` au controlleur, qui peut enfin l'envoyer au front.

## Cache

L'application dispose d'un système de cache afin de ne pas trop solliciter l'API et d'améliorer le temps de réponse. Il existe 3 caches :
- Le cache `requetes` qui stocke les réponses de l'API pour les dates futures. Ce cache est renouvellé de temps en temps.
- Le cache `permanant` qui stocke les réponses de l'API pour les dates passées. Ce cache n'est pas renouvellé.
- Le cache `erreur` qui stocke les réponses en erreur de l'API. Ce cache est renouvellé très régulièrement.

La logique du cache est gérée directement dans le classe `APIClient`. C'est elle qui va regarder si les objets sont dans le cache avant de faire les requêtes, et stocker les réponses de l'API dans le cache correspondant.

# Déploiement

## En Local

Avant tout, il faut commencer par compléter la configuration de l'application et notamment les informations relatives au ssl et à l'API avec laquelle communiquer. Intellij est recommandé pour un lancement en local : il suffit d'ajouter une configuration d'application sur la classe `fr.recia.menucantine.MenuCantineApplication` après avoir ouvert le projet.
L'application est alors accessible sur l'URL suivante : https://localhost:8443/menuCantine/demo/demo.html

## Nouvelle version du front

Pour tester une nouvelle version du front avec le back, il suffit de copier les fichiers `.js` compilés dans le dossier `ressources/static/demo` (attention à ne pas toucher le fichier `demo.html`).

## Constuire un war

Pour constuire un war, il suffit de faire un simple `mvn clean package`.
Les tests unitaires selons lancés automatiquement, et le war sera disponible dans `target`.

## Pousser une snapshot

Pour push une snapshot, il suffit de faire un `mvn clean package deploy` (attention à bien vérifier dans le `pom.xml` qu'on est bien sur une version de test).

## Pousser une release

Pour push une release, on commence par un `mvn release:clean release:prepare` puis on fait un `mvn realease:perfrom`.

## Gestion des licenses

Pour vérifier les entêtes de fichier : `mvn license:check`.
Pour ajouter les entêtes de fichier : `mvn license:format`.
Le template de la license ajoutée est dans `etc/header.template`.

# Configuration

La configuration se trouve dans le fichier `application.yml` dans les ressources. Elle doit **impérativement** être complétée avant de pouvoir lancer l'application, même en local. Il faut compléter au minimum les valeurs pour le ssl ainsi que les identifiants pour l'Api. Le reste des valeurs peuvent être laisssées par défaut dans un premier temps.

| Propriété                          | Signification                                                                             | Valeur par défaut                 |
|------------------------------------|-------------------------------------------------------------------------------------------|-----------------------------------|
| server.port                        | Port du serveur (ici en https)                                                            | 8443                              |
| servlet.ssl.key-store              | Nom de l'entrepôt stockant la clé                                                         | *à compléter*                     |
| servlet.ssl.key-store-password     | Mot de passe de l'entrepôt stockant la clé                                                | *à compléter*                     |
| servlet.ssl.key-store-type         | Type de clé                                                                               | *à compléter*                     |
| servlet.ssl.key-alias              | Nom de la clé                                                                             | *à compléter*                     |
| servlet.ssl.key-password           | Mot de passe de la clé                                                                    | *à compléter*                     |
| server.servlet.context-path        | Path du servlet                                                                           | /menuCantine                      |
| soffit.jwt.signatureKey            | Clé pour le soffit                                                                        | *à compléter*                     |
| adoria.gemrcn-csv                  | Chemin vers le fichier contenant les gemrcn à charger                                     | classpath:GemRcn.csv              |
| adoria.labels-csv                  | Chemin vers le fichier contenant les labels à charger                                     | classpath:labels.csv              |
| api.initial-query-url              | URL complète de l'API sur laquelle on récupère une URL dynamique par UAI                  | https://api.webgerest.fr/url      |
| api.auth-endpoint                  | Endpoint sur lequelle on doit faire une requête pour s'authentifier                       | /auth                             |
| api.menu-endpoint                  | Endpoint sur lequelle on doit faire une requête pour récupérer un menu                    | /menus                            |
| api.client_id                      | L'identifiant permettant de s'authentifier pour récupérer un token                        | *à compléter*                     |
| api.menu-endpoint                  | Le mot de passe permettant de s'authentifier pour récupérer un token                      | *à compléter*                     |
| logging.level.fr.recia.menucantine | Niveau de log en local                                                                    | debug                             |
| spring.cache.type                  | La librairie utilisée pour la gestion du cache                                            | ehcache                           |
| mapper.services                    | Un dictionnaire des services avec le nom et le numéro de chaque service                   | *à voir directement dans la conf* |
| mapper.sousmenus                   | Un dictionnaire des sous-menus avec le nom, le nom final et le numéro de chaque sous-menu | *à voir directement dans la conf* |

# Tests unitaires
Les tests unitaires sont lancés avec un profil `unit` qui utilise une configuration particulière `application-test.yml` qui désactive le cache. Certaines classes telles que `RestClientCertConfiguration` ne sont également pas chargées lorsqu'on utilise le profil de test afin de pouvoir passer la sécurité lorsqu'on veut tester le controlleur. Les différentes classes pour les tests sont les suivantes :
```  
test  
├── java                                  
│   ├── beans                                  # Tests sur les beans adoria (méthodes clean et complete)
│   │   ├── BeanJourneeTests.java
│   │   ├── BeanPlatTests.java
│   │   ├── BeanSemaineTests.java
│   │   └── BeanServiceTests.java
│   └── mapper                                  # Tests de la transformation des DTO en beans adoria
│   │   └── MapperWebgerestTests.java
│   ├── MenuCantineApplicationTests.java
│   ├── MenuCantineControllerTests.java
│   └── MenuCantineServiceTests.java 
```  
On utilise `mockito` pour mocker les dépendances utiles lorsqu'on teste le service ou le controlleur. Dans le cadre du service, on mock les appels à l'API et au mapper, et dans le cas du controlleur, on map les appels au service.

Le rapport du coverage des tests unitaires est disponible sous `target/coverage-reports/jacoco-unit.exec`.