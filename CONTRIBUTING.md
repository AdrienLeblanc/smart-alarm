# Guide de contribution - SmartAlarm

Merci de votre intérêt pour contribuer à SmartAlarm ! Ce document vous guidera à travers le processus de contribution.

## 🚀 Comment contribuer

### 1. Fork et Clone
```bash
# Fork le repository sur GitHub
# Puis clonez votre fork
git clone https://github.com/VOTRE_USERNAME/SmartAlarm.git
cd SmartAlarm
```

### 2. Créer une branche
```bash
git checkout -b feature/ma-nouvelle-fonctionnalite
# ou
git checkout -b fix/correction-bug
```

### 3. Conventions de nommage

#### Branches
- `feature/` - Nouvelles fonctionnalités
- `fix/` - Corrections de bugs
- `refactor/` - Refactoring de code
- `docs/` - Documentation
- `test/` - Ajout de tests

#### Commits
Format : `type(scope): description`

Types :
- `feat` - Nouvelle fonctionnalité
- `fix` - Correction de bug
- `refactor` - Refactoring
- `docs` - Documentation
- `test` - Tests
- `style` - Formatage
- `chore` - Maintenance

Exemples :
```bash
git commit -m "feat(alarm): ajouter le time picker Material3"
git commit -m "fix(scheduler): corriger le calcul du prochain jour de répétition"
git commit -m "docs(readme): mettre à jour les instructions d'installation"
```

### 4. Standards de code

#### Kotlin
- Suivre les [conventions Kotlin](https://kotlinlang.org/docs/coding-conventions.html)
- Utiliser ktlint pour le formatage
- Maximum 120 caractères par ligne
- Indentation : 4 espaces

#### Compose
- Un composable par fichier (sauf helpers)
- Nommer les composables en PascalCase
- Préfixer les previews avec le nom du composable

```kotlin
@Composable
fun AlarmCard(...) { }

@Preview
@Composable
fun AlarmCardPreview() { }
```

#### Documentation
- Documenter les fonctions publiques
- Utiliser KDoc pour les commentaires

```kotlin
/**
 * Planifie une alarme avec AlarmManager
 * 
 * @param alarm L'alarme à planifier
 * @throws SecurityException Si les permissions ne sont pas accordées
 */
fun scheduleAlarm(alarm: Alarm) { }
```

### 5. Tests

#### Tests unitaires
Créer des tests pour :
- ViewModels
- Repositories
- Business logic

```kotlin
class AlarmRepositoryTest {
    @Test
    fun `saveAlarm should insert alarm in database`() = runTest {
        // Arrange
        val alarm = Alarm(...)
        
        // Act
        repository.saveAlarm(alarm)
        
        // Assert
        val saved = repository.getAlarmById(alarm.id)
        assertEquals(alarm, saved)
    }
}
```

#### Tests UI
Utiliser Compose Testing

```kotlin
@Test
fun clickOnFab_opensAlarmEditScreen() {
    composeTestRule.setContent {
        AlarmListScreen(...)
    }
    
    composeTestRule.onNodeWithContentDescription("Ajouter une alarme").performClick()
    // Vérifier la navigation
}
```

### 6. Pull Request

#### Checklist avant de soumettre
- [ ] Le code compile sans erreur
- [ ] Les tests passent
- [ ] Le code est formaté correctement
- [ ] La documentation est à jour
- [ ] Pas de code commenté inutile
- [ ] Pas de `TODO` ou `FIXME` non résolus
- [ ] Les strings sont dans `strings.xml`

#### Description du PR
Template :
```markdown
## Description
Brève description de ce qui a été changé et pourquoi.

## Type de changement
- [ ] Bug fix
- [ ] Nouvelle fonctionnalité
- [ ] Breaking change
- [ ] Documentation

## Tests effectués
- [ ] Tests unitaires
- [ ] Tests UI
- [ ] Tests manuels sur émulateur
- [ ] Tests sur appareil physique

## Captures d'écran (si UI)
[Ajouter des captures d'écran si pertinent]

## Checklist
- [ ] Mon code suit les conventions du projet
- [ ] J'ai ajouté des tests
- [ ] J'ai mis à jour la documentation
- [ ] Mes commits suivent les conventions
```

### 7. Revue de code

Votre PR sera examiné selon ces critères :
- Qualité du code
- Respect des conventions
- Tests adéquats
- Documentation claire
- Performance
- Accessibilité

### 8. Après la revue

Si des changements sont demandés :
```bash
# Faire les modifications
git add .
git commit -m "fix: adresse les commentaires de la revue"
git push
```

Le PR sera automatiquement mis à jour.

## 📋 Domaines de contribution

### Priorités actuelles
Consultez `TODO.md` pour les tâches en cours. Les contributions sont particulièrement bienvenues pour :

1. **MVP** (Haute priorité)
   - Time Picker Material3
   - AlarmScheduler integration
   - AlarmService implementation
   - Tests unitaires

2. **Fonctionnalités** (Moyenne priorité)
   - Sélecteur de sonnerie
   - Détection par capteurs
   - BootReceiver
   - Notifications

3. **Améliorations** (Basse priorité)
   - Injection de dépendances (Hilt)
   - Animations
   - Localisation
   - Thème dynamique

### Domaines techniques

#### UI/UX
- Design des écrans
- Animations et transitions
- Accessibilité
- Responsive design

#### Backend
- Logique métier
- Gestion des alarmes
- Capteurs
- Notifications

#### Tests
- Tests unitaires
- Tests d'intégration
- Tests UI
- Tests de performance

#### Documentation
- Guides
- Tutoriels
- Exemples de code
- Architecture

## 🐛 Signaler un bug

### Template de bug report
```markdown
**Description du bug**
Description claire et concise du bug.

**Étapes pour reproduire**
1. Aller à '...'
2. Cliquer sur '...'
3. Scroller jusqu'à '...'
4. Voir l'erreur

**Comportement attendu**
Ce qui devrait se passer.

**Comportement actuel**
Ce qui se passe réellement.

**Captures d'écran**
Si applicable, ajoutez des captures d'écran.

**Environnement**
- Appareil : [ex. Pixel 7]
- Version Android : [ex. 13]
- Version de l'app : [ex. 1.0.0]

**Logs**
Collez les logs pertinents ici.

**Contexte additionnel**
Toute autre information utile.
```

## 💡 Proposer une fonctionnalité

### Template de feature request
```markdown
**Le problème**
Décrivez le problème que cette fonctionnalité résoudrait.

**La solution proposée**
Décrivez comment vous imaginez la fonctionnalité.

**Alternatives considérées**
Décrivez les alternatives que vous avez considérées.

**Contexte additionnel**
Ajoutez tout autre contexte ou captures d'écran.

**Impact**
- Impact sur les utilisateurs : [élevé/moyen/faible]
- Complexité technique : [élevée/moyenne/faible]
- Priorité suggérée : [haute/moyenne/basse]
```

## 📞 Communication

### Channels
- **Issues** : Pour les bugs et feature requests
- **Pull Requests** : Pour les contributions de code
- **Discussions** : Pour les questions générales

### Temps de réponse
- Issues : 2-3 jours
- Pull Requests : 3-5 jours
- Questions : 1-2 jours

## 🏆 Contributeurs

Tous les contributeurs seront ajoutés au fichier `CONTRIBUTORS.md`.

## 📜 Licence

En contribuant, vous acceptez que vos contributions soient sous la même licence que le projet.

## ❓ Questions

Si vous avez des questions, n'hésitez pas à :
1. Consulter la documentation (`ARCHITECTURE.md`, `DEVELOPMENT.md`)
2. Créer une Discussion sur GitHub
3. Demander dans les commentaires d'un issue existant

---

**Merci de contribuer à SmartAlarm ! 🎉**
