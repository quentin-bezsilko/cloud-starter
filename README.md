# Cloud Starter - Spring Boot DevOps Demo

## Présentation

Cloud Starter est un projet de démonstration permettant d'illustrer la mise en place d'une chaîne CI/CD moderne autour d'une application Spring Boot.

Le projet met en œuvre :

- Spring Boot 4
- Java 25
- Maven
- Spotless
- JUnit 5
- JaCoCo
- SonarQube
- Docker
- Trivy
- Kubernetes (Minikube)
- Kustomize

---

# Architecture CI/CD

```text
GitLab
  |
  +--> Spotless
  |
  +--> Build Maven
  |
  +--> Tests Unitaires
  |
  +--> JaCoCo
  |
  +--> SonarQube
  |
  +--> Build Docker
  |
  +--> Trivy Security Scan
  |
  +--> Kubernetes
```

---

# Description des étapes CI/CD

## Spotless

Vérifie le formatage du code source Java.

Objectifs :

- Uniformiser le code
- Éviter les conflits de formatage
- Imposer un standard de développement

Commande exécutée :

```bash
mvn spotless:check
```

---

## Build

Compilation du projet.

Commande exécutée :

```bash
mvn clean compile
```

Objectifs :

- Vérifier que le projet compile
- Détecter rapidement les erreurs de syntaxe

---

## Tests

Exécution des tests unitaires JUnit.

Commande exécutée :

```bash
mvn test
```

Objectifs :

- Vérifier le comportement métier
- Éviter les régressions

---

## JaCoCo

Analyse de couverture de code.

Commande exécutée :

```bash
mvn verify
```

Objectifs :

- Générer un rapport de couverture
- Garantir un taux minimal de couverture

Rapport généré :

```text
target/site/jacoco/index.html
```

Politique actuelle :

```text
Couverture minimale : 80 %
```

---

## SonarQube

Analyse statique du code.

Objectifs :

- Code Smells
- Vulnerabilities
- Bugs
- Dette technique
- Quality Gate

Commande :

```bash
mvn sonar:sonar
```

Remarque :

L'instance SonarQube est actuellement hébergée localement.

Le job est conservé dans la pipeline mais ne peut être exécuté automatiquement depuis les runners GitLab.com sans exposer SonarQube ou installer un runner local.

---

## Configuration GitLab pour SonarQube

Le job SonarQube utilise des variables GitLab afin d'éviter de stocker des secrets dans le dépôt.

Dans GitLab :

```text
Settings
→ CI/CD
→ Variables
```

Créer les variables suivantes :

### SONAR_HOST_URL

Adresse de l'instance SonarQube.

Exemples :

```text
http://localhost:9000
```

ou

```text
http://host.docker.internal:9000
```

ou

```text
https://sonar.mondomaine.fr
```

Remarque :

Les runners GitLab.com ne peuvent pas accéder à un SonarQube hébergé localement sur un poste de travail.

Dans ce cas :

- installer un GitLab Runner local ;
- ou exposer SonarQube sur le réseau ;
- ou utiliser SonarCloud.

---

### SONAR_TOKEN

Token généré dans SonarQube.

Création :

```text
My Account
→ Security
→ Generate Token
```

Exemple :

```text
sqp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

Copiez cette valeur dans la variable GitLab :

```text
SONAR_TOKEN
```

Type recommandé :

```text
Masked
Protected
```

---

### Vérification locale

Avant de lancer la pipeline, il est recommandé de vérifier que SonarQube est opérationnel :

```bash
mvn clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=<TOKEN>
```

L'analyse doit apparaître dans l'interface SonarQube.

---

### Intégration dans la pipeline

Le job utilise les variables GitLab :

```yaml
sonar:
  stage: quality

  script:
    - >
      mvn sonar:sonar
      -Dsonar.host.url=$SONAR_HOST_URL
      -Dsonar.token=$SONAR_TOKEN
      -Dsonar.qualitygate.wait=true
```

Le code reste ainsi indépendant de l'environnement d'exécution.
---

## Docker

Construction d'une image Docker multi-stage.

Commande :

```bash
docker build -t cloudstarter:latest .
```

Objectifs :

- Créer une image légère
- Préparer le déploiement Kubernetes

---

## Trivy

Analyse de sécurité de l'image Docker.

Objectifs :

- Détection des vulnérabilités système
- Détection des packages vulnérables
- Validation de l'image avant déploiement

Politique actuelle :

```text
Blocage uniquement sur les vulnérabilités CRITICAL
```

Commande :

```bash
trivy image --severity CRITICAL --exit-code 1 --input image.tar
```

---

# Architecture Kubernetes

## Arborescence

```text
k8s
├── base
│   ├── deployment.yml
│   ├── hpa.yml
│   ├── ingress.yml
│   ├── namespace.yml
│   ├── service.yml
│   └── kustomization.yml
│
└── overlays
    └── dev
        ├── configmap.yml
        ├── secret.yml
        └── kustomization.yml
```

---

# Description des ressources Kubernetes

## Namespace

```yaml
kind: Namespace
```

Permet d'isoler l'ensemble des ressources du projet.

Exemple :

```text
cloudstarter
```

---

## ConfigMap

```yaml
kind: ConfigMap
```

Contient la configuration applicative.

Exemples :

```text
SPRING_APPLICATION_NAME
SERVER_PORT
SPRING_DATASOURCE_URL
SPRING_JPA_SHOW_SQL
```

Objectif :

- Externaliser la configuration
- Éviter les rebuilds d'image

---

## Secret

```yaml
kind: Secret
```

Contient les données sensibles.

Exemple :

```text
SPRING_DATASOURCE_PASSWORD
```

Objectif :

- Séparer les secrets de la configuration
- Faciliter la rotation des secrets

---

## Deployment

```yaml
kind: Deployment
```

Responsable :

- Du cycle de vie des Pods
- Du nombre de réplicas
- Des mises à jour

Configuration actuelle :

```text
2 réplicas
```

---

## Service

```yaml
kind: Service
```

Expose l'application à l'intérieur du cluster.

Objectifs :

- Découverte réseau
- Équilibrage de charge

---

## Ingress

```yaml
kind: Ingress
```

Expose l'application en HTTP.

Permet :

- Le routage
- Les noms de domaine
- La centralisation des accès

---

## HPA

```yaml
kind: HorizontalPodAutoscaler
```

Objectifs :

- Augmenter le nombre de pods en cas de charge
- Réduire automatiquement le nombre de pods

Configuration actuelle :

```text
Min Replica : 2
Max Replica : 5
Target CPU : 70 %
```

---

## Readiness Probe

Vérifie si l'application est prête à recevoir du trafic.

Endpoint :

```text
/cloudstarter/actuator/health/readiness
```

---

## Liveness Probe

Vérifie si l'application est toujours vivante.

Endpoint :

```text
/cloudstarter/actuator/health/liveness
```

---

# Déploiement de l'application

## Démarrer Minikube

```bash
minikube start
```

---

## Construire l'image Docker

```bash
docker build -t cloudstarter:latest .
```

---

## Vérifier la configuration Kustomize

```bash
kubectl kustomize k8s/overlays/dev
```

Cette commande affiche le manifeste Kubernetes final résultant de la fusion du dossier :

```text
base
```

et de l'overlay :

```text
dev
```

---

## Déployer l'application

```bash
kubectl apply -k k8s/overlays/dev
```

L'option :

```text
-k
```

indique à Kubernetes d'utiliser Kustomize.

Cette commande crée automatiquement :

- Namespace
- ConfigMap
- Secret
- Deployment
- Service
- Ingress
- HPA

et démarre les Pods.

---

## Vérifier le déploiement

Visualiser les Pods :

```bash
kubectl get pods
```

Visualiser les Services :

```bash
kubectl get svc
```

Visualiser les Ingress :

```bash
kubectl get ingress
```

Visualiser les HPA :

```bash
kubectl get hpa
```

Visualiser toutes les ressources :

```bash
kubectl get all
```

---

## Consulter les logs

```bash
kubectl logs -l app=cloudstarter
```

---

## Supprimer l'environnement

```bash
kubectl delete -k k8s/overlays/dev
```

---

# Fonctionnalités DevOps démontrées

- ✅ GitLab CI/CD
- ✅ Spotless
- ✅ JUnit 5
- ✅ JaCoCo
- ✅ SonarQube
- ✅ Docker Multi-stage
- ✅ Trivy
- ✅ Kubernetes
- ✅ ConfigMap
- ✅ Secret
- ✅ Service
- ✅ Ingress
- ✅ HPA (Horizontal Pod Autoscaler)
- ✅ Readiness Probe
- ✅ Liveness Probe
- ✅ Kustomize

Cette démonstration illustre une chaîne DevOps moderne couvrant la qualité, la sécurité, la conteneurisation et l'orchestration d'une application Spring Boot.
