# Git-Tutorial
## .md = Markdown = Formatierungs-Sprache

- [x] Git-Repo anlegen
- [ ] Git Basic Befehle
- [ ] Kollaboration

Weiteres zu Markdown: [hier](https://docs.github.com/de/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax)

```
git init
git add .
git commit -m "Message"
```
> Merge Konflikt provozieren:
> - Lokal in Branch (hier in main) code ändern  
> - An derselben Stelle Remote Code ändern (Simulation anderer Programmierer)
> - git pull liefert Merge Konflikt

> Merge Konflikt lösen
> - In Intellij in angezeigter Leiste: resolve conflicts
> - In angezeigten 3 Spalten linke oder rechte Variante auswählen
> - git commit -m "Solved"
> - git push -u origin main