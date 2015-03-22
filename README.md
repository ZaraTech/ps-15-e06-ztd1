# SmartCatalogue
## Como preparar workspace:

1. Preparar eclipse:
  1. Descargar [eclipse luna](https://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/lunasr2) o descarga las versiones preparadas en la sección de descargas (De usar una versión preparada saltar directamente al paso 2)
  2. Instalar plugins:
    1. [EGIT](http://eclipse.org/egit/) (Opcional)
    2. [m2e-android](http://rgladwell.github.io/m2e-android/)
2. Preparar SDK:
  1. Descargar [SDK Tools](http://developer.android.com/sdk/index.html)
  2. Instalar desde el SDK manager al menos las SDK de la version 16 y 22
  3. Establecer la variable de entorno ANDROID_HOME con la ruta donde esten las SDK Tools
3. Clonar repositorio.
4. Importar como proyecto Maven. 
5. Ejecutar proyecto: botón secundario en el proyecto >> run as >> maven build >> run

## Descargas
* Vesiones de eclipse preparadas:
  * [Eclipse Windows 64bits](https://www.dropbox.com/s/5bm22pbgfsp1526/eclipse-Windows-64bits.zip?dl=0)
  * [Eclipse Linux 64bits](https://www.dropbox.com/s/svc97nkcnbbho5t/eclipse-Linux-64bits.zip?dl=0)

## Solución de problemas
* Aparece un mensaje de error al importar el proyecto: Para solucionar el error que aparece únicamente se debe poner el cursor sobre el error y elegir la 2º solución que ofrece eclipse.
* Errores al ejecutar:

> [ERROR] Failed to execute goal com.jayway.maven.plugins.android.generation2:android-maven-plugin:3.8.2:deploy (default-cli) on project SmartCatalogue: No online devices attached. -> [Help 1]

Este error se produce al no haber iniciado ningún dispositivo. Para solucionarlo únicamente se debe [emular un dispositivo](http://www.aprendeandroid.com/l1/emulador.htm) o [conectar un dispositivo por usb](http://www.aprendeandroid.com/l1/conectar_movil_eclipse.htm)

## Guías:
* [EGIT](http://www.notodocodigo.com/integracion-continua/git-clonar-repositorio-y-manejo-de-ramas/)
