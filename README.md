# Pitstop - App Móvil de Seguimiento de Mantenimiento Vehicular

## Descripción del Proyecto

Pitstop es una aplicación móvil nativa para Android desarrollada en Java que ayuda a los usuarios a dar seguimiento al programa de mantenimiento de su vehículo. La aplicación permite registrar el kilometraje y el historial de mantenimiento, calcula automáticamente cuándo vence el próximo servicio y notifica al usuario con antelación.

## Características Principales

### Funcionalidades Esenciales (Must Have)
- ✅ **Autenticación de Usuario**: Registro e inicio de sesión usando Firebase Authentication
- ✅ **CRUD Completo para Registros de Mantenimiento**: Crear, leer, actualizar y eliminar registros
- ✅ **Seguimiento de Kilometraje**: Registrar y mostrar el kilometraje actual del vehículo
- ✅ **Cálculo del Próximo Servicio**: Cálculo automático basado en periodicidad y kilometraje

### Funcionalidades Altamente Deseables (Should Have)
- ✅ **Historial de Mantenimiento**: Pantalla dedicada con búsqueda y filtros
- ✅ **Integración de Sensores**:
  - GPS para seguimiento de viajes
  - Cámara para captura de odómetro con OCR básico

### Funcionalidades Opcionales (Could Have)
- ⏳ **Notificaciones Locales**: Recordatorios cuando un servicio se aproxima

## Stack Tecnológico

- **Lenguaje**: Java
- **UI Toolkit**: Vistas de Android tradicionales (XML Layouts) con Fragments
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Base de Datos**: Room (wrapper de SQLite)
- **Autenticación**: Firebase Authentication
- **Operaciones Asíncronas**: LiveData y Executors
- **SDK Mínimo**: API 24 (Android 7.0 Nougat)

## Estructura del Proyecto

```
app/
├── src/main/java/com/example/pitstop/
│   ├── database/
│   │   ├── entity/          # Entidades de Room
│   │   ├── dao/             # Interfaces DAO
│   │   └── AppDatabase.java # Configuración de Room
│   ├── repository/          # Patrón Repository
│   ├── viewmodel/           # ViewModels MVVM
│   ├── ui/
│   │   ├── fragments/       # Fragmentos de UI
│   │   └── adapters/        # Adaptadores RecyclerView
│   └── MainActivity.java    # Actividad principal
├── src/main/res/
│   ├── layout/              # Layouts XML
│   ├── drawable/            # Iconos y recursos gráficos
│   ├── navigation/          # Navegación
│   └── values/              # Strings y recursos
└── google-services.json     # Configuración Firebase
```

## Entidades de Base de Datos

### User
- `uid` (String, PK) - ID de Firebase Auth
- `email` (String) - Correo electrónico

### Maintenance
- `id` (int, PK, Auto) - ID único
- `userUid` (String, FK) - Referencia a User
- `type` (String) - Tipo de mantenimiento
- `description` (String) - Descripción detallada
- `periodicityKm` (int) - Intervalo en kilómetros
- `executedKm` (int) - Kilometraje cuando se ejecutó
- `date` (long) - Fecha como timestamp
- `cost` (Double, nullable) - Costo del servicio
- `notes` (String, nullable) - Notas adicionales

### VehicleLog
- `id` (int, PK, Auto) - ID único
- `userUid` (String, FK) - Referencia a User
- `currentKm` (int) - Kilometraje actual
- `date` (long) - Fecha como timestamp
- `odometerPhotoUri` (String, nullable) - URI de foto del odómetro

## Pantallas de la Aplicación

1. **Splash Screen**: Logo de la app al inicio
2. **Login/Registro**: Formularios de autenticación
3. **Dashboard**: 
   - Kilometraje actual
   - Lista de próximos mantenimientos
   - Mantenimientos recientes
4. **Lista de Mantenimientos**: Vista completa con búsqueda y filtros
5. **Formulario de Mantenimiento**: Crear/editar registros
6. **Detalle de Mantenimiento**: Vista detallada de un registro
7. **Sensores**: GPS y cámara para seguimiento
8. **Ajustes**: Información del usuario y configuración

## Instalación y Configuración

### Prerrequisitos
- Android Studio Arctic Fox o superior
- Android SDK API 24+
- Cuenta de Firebase

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone <repository-url>
   cd Pitstop
   ```

2. **Configurar Firebase**
   - Crear un proyecto en [Firebase Console](https://console.firebase.google.com/)
   - Habilitar Authentication con Email/Password
   - Descargar `google-services.json` y reemplazar el archivo placeholder
   - Actualizar el `applicationId` en `build.gradle.kts` si es necesario

3. **Sincronizar dependencias**
   ```bash
   ./gradlew build
   ```

4. **Ejecutar la aplicación**
   - Conectar dispositivo Android o iniciar emulador
   - Ejecutar desde Android Studio

## Uso de la Aplicación

### Registro e Inicio de Sesión
1. Abrir la aplicación
2. Crear cuenta con email y contraseña
3. O iniciar sesión con credenciales existentes

### Gestión de Mantenimientos
1. **Agregar Mantenimiento**:
   - Tocar el botón "+" en Dashboard o Lista
   - Completar formulario con tipo, descripción, periodicidad, etc.
   - Guardar

2. **Ver Próximos Servicios**:
   - Dashboard muestra automáticamente los próximos servicios
   - Basado en kilometraje actual y periodicidad

3. **Actualizar Kilometraje**:
   - Tocar "Actualizar Kilometraje" en Dashboard
   - Entrada manual o foto del odómetro

### Sensores
1. **GPS**: Iniciar viaje para rastrear distancia
2. **Cámara**: Tomar foto del odómetro para actualizar kilometraje

## Permisos Requeridos

- `INTERNET` - Para Firebase Authentication
- `ACCESS_FINE_LOCATION` - Para seguimiento GPS
- `ACCESS_COARSE_LOCATION` - Para seguimiento GPS
- `CAMERA` - Para captura de odómetro
- `WRITE_EXTERNAL_STORAGE` - Para guardar fotos (API ≤ 28)
- `READ_EXTERNAL_STORAGE` - Para acceder a fotos (API ≤ 32)
- `POST_NOTIFICATIONS` - Para notificaciones locales

## Características Técnicas

### Arquitectura MVVM
- **Model**: Entidades de Room y repositorios
- **View**: Fragments y layouts XML
- **ViewModel**: Lógica de presentación y estado

### Base de Datos Room
- Persistencia local con SQLite
- Relaciones entre entidades con claves foráneas
- Operaciones asíncronas con LiveData

### Firebase Integration
- Autenticación segura con email/contraseña
- Persistencia de sesión
- Manejo de errores de red

### UI/UX
- Material Design 3
- Navegación con Bottom Navigation
- RecyclerViews para listas eficientes
- Formularios con validación
- Estados de carga y error

## Próximas Mejoras

- [ ] Notificaciones locales para recordatorios
- [ ] Sincronización en la nube
- [ ] Exportación de datos
- [ ] Múltiples vehículos
- [ ] Estadísticas y gráficos
- [ ] Integración con servicios de mantenimiento

## Contribución

1. Fork el proyecto
2. Crear rama para feature (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

## Contacto

Para preguntas o sugerencias, contactar al equipo de desarrollo.

---

**Nota**: Este es un proyecto de demostración. Para uso en producción, se recomienda implementar validaciones adicionales, pruebas unitarias y de integración, y optimizaciones de rendimiento.
