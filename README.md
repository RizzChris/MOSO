# MOSO.app

**MOSO.app** es una plataforma de compraventa de componentes electrónicos, pensada para makers, estudiantes e ingenieros que buscan piezas de calidad a buen precio.

---

## 📋 Tabla de contenidos

1. [Características](#-características)  
2. [Tecnologías](#-tecnologías)  
3. [Arquitectura y estructura de carpetas](#-arquitectura-y-estructura-de-carpetas)  
4. [Instalación y puesta en marcha](#-instalación-y-puesta-en-marcha)  
5. [Uso](#-uso)  
6. [Despliegue](#-despliegue)  
7. [Contribuciones](#-contribuciones)  
8. [Licencia](#-licencia)

---

## 🔥 Características

- **Catálogo** de categorías y productos  
- **Carrusel** de imágenes destacado en Home  
- **Autenticación** (email/password, Google, Facebook)  
- **Perfil** de usuario con estadísticas (publicaciones, compras, ventas)  
- **Carrito de compras**: agregar, ver total y finalizar compra  
- **Historial de órdenes** y detalle de compra con vendedor e imagen  
- **Chat** interno entre compradores y vendedores  
- **Ajustes**: tema oscuro, notificaciones, idioma…

---

## 🛠 Tecnologías

- **Kotlin & Jetpack Compose**  
- **Firebase Authentication**  
- **Cloud Firestore** (productos, carrito, órdenes, chat)  
- **Firebase Storage** (imágenes de producto)  
- **Coil** para carga de imágenes  
- **DataStore Preferences** para ajustes locales  
- **Dokka** para generación de documentación

---

## 🏗 Arquitectura y estructura de carpetas

app/
├─ src/main/java/com/example/moso/
│ ├─ data/
│ │ ├─ model/ ← Clases de datos (User, Product, Order…)
│ │ └─ repository/ ← Lógica de acceso a Firebase
│ ├─ ui/
│ │ ├─ components/ ← Composables reutilizables (cards, headers…)
│ │ ├─ navigation/ ← NavHost, Drawer, BottomBar
│ │ └─ screens/ ← Pantallas (Home, Cart, Profile…)
│ └─ theme/ ← Colores, tipografías, estilos
└─ build.gradle(.kts) ← Configuración de Gradle, plugins, dependencias

---

## ⚙️ Instalación y puesta en marcha

1. **Clona** el repositorio  
   ```bash
   Configura Firebase
2. Crea un proyecto en Firebase Console
  Agrega tu google-services.json a app/
  Habilita Authentication (Email/Google/Facebook), Firestore, Storage
  Ajusta las reglas de seguridad en Firestore y Storage
     git clone https://github.com/tu-usuario/moso-app.git
     cd moso-app
3. Compila la app
      ./gradlew clean assembleDebug
4. Ejecuta en un emulador o dispositivo físico.

---

## 🎮 Uso
- Al iniciar la app podrás:

- Registrarte o Iniciar sesión

- Explorar el catálogo por categorías

- Ver detalles de producto y agregar al carrito

- Concluir una compra y ver tu historial

- Chatear con vendedores

- Consultar y editar tu perfil

- Ajustar preferencias (tema, notificaciones…)

---

## 🚀 Despliegue

Para generar un APK de producción:
  ./gradlew assembleRelease

---

## 🤝 Contribuciones
  - Haz un fork

  - Crea una rama (git checkout -b feature/nueva-funcion)

  - Commit tus cambios (git commit -m "Agrega …")

  - Push (git push origin feature/nueva-funcion)

  - Abre un Pull Request

---


## 📄 Licencia
Este proyecto está bajo la licencia MIT.

---

Construido con ❤️ por el equipo de MOSO.app
¡Esperamos tus comentarios y contribuciones!
