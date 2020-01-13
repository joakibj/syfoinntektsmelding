# Syfoinntektsmelding

[![Actions Status](https://github.com/navikt/syfoinntektsmelding/workflows/Bygg%20og%20deploy%20til%20prod/badge.svg)](https://github.com/navikt/syfoinntektsmelding/actions)

## Funksjonalitet
Oppgaven til syfoinntektsmelding er å lytte på en kø med nye inntektsmeldinger. Når den finner en ny inntektsmelding skal appen opprette eller oppdatere en oppgave i GOSYS for så å ferdigstille en journalpost med oppdatert informasjon.

## Diagram
![image](https://user-images.githubusercontent.com/3852471/39524448-5147afc0-4e19-11e8-95bb-36c5f57b8d70.png)

## For å starte appen lokalt:
Applikasjonen startes i LocalApplication.class Den må kjøre som en spring-boot applikasjon med local som profil. 
Du må også ha en `application-local.properties` som peker på eksterne avhenigheter. Denne fås av en annen utvikler.

## For NAV-ansatte
Interne henvendelser kan sendes via Slack i kanalen #helse-arbeidsgiver
