
import java.io.*;
import java.util.Scanner;

class Cancion implements Serializable {
    String titulo;
    String artista;
    int duracion;
    Cancion siguiente;

    public Cancion(String titulo, String artista, int duracion) {
        this.titulo = titulo;
        this.artista = artista;
        this.duracion = duracion;
        this.siguiente = null;
    }
}

class Playlist implements Serializable {
    String nombre;
    Cancion inicio;

    public Playlist(String nombre) {
        this.nombre = nombre;
        this.inicio = null;
    }

    public void agregarCancion(String titulo, String artista, int duracion) {
        if (titulo == null || titulo.trim().isEmpty() || artista == null || artista.trim().isEmpty() || duracion <= 0) {
            System.out.println("Datos inválidos. Asegúrese de ingresar título, artista y duración válidos.\n");
            return;
        }
        Cancion nueva = new Cancion(titulo, artista, duracion);
        if (inicio == null) {
            inicio = nueva;
        } else {
            Cancion actual = inicio;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nueva;
        }
        System.out.println("Canción agregada correctamente.\n");
    }

    public void mostrarCanciones() {
        if (inicio == null) {
            System.out.println("La playlist está vacía.\n");
            return;
        }
        Cancion actual = inicio;
        System.out.println("Canciones en la playlist:");
        while (actual != null) {
            System.out.println("- " + actual.titulo + " | " + actual.artista + " | " + actual.duracion + " segundos");
            actual = actual.siguiente;
        }
        System.out.println();
    }

    public void buscarCancion(String titulo) {
        Cancion actual = inicio;
        while (actual != null) {
            if (actual.titulo.equalsIgnoreCase(titulo)) {
                System.out.println("Canción encontrada:");
                System.out.println("- " + actual.titulo + " | " + actual.artista + " | " + actual.duracion + " segundos\n");
                return;
            }
            actual = actual.siguiente;
        }
        System.out.println("Canción no encontrada.\n");
    }

    public void eliminarCancion(String titulo) {
        if (inicio == null) {
            System.out.println("La playlist está vacía.\n");
            return;
        }
        if (inicio.titulo.equalsIgnoreCase(titulo)) {
            inicio = inicio.siguiente;
            System.out.println("Canción eliminada correctamente.\n");
            return;
        }
        Cancion actual = inicio;
        while (actual.siguiente != null && !actual.siguiente.titulo.equalsIgnoreCase(titulo)) {
            actual = actual.siguiente;
        }
        if (actual.siguiente != null) {
            actual.siguiente = actual.siguiente.siguiente;
            System.out.println("Canción eliminada correctamente.\n");
        } else {
            System.out.println("Canción no encontrada.\n");
        }
    }
}

public class Main {
    static Scanner sc = new Scanner(System.in);
    static final String ARCHIVO = "playlist.dat";

    public static void main(String[] args) {
        Playlist playlist = cargarDatos();
        int opcion = -1;

        do {
            limpiarConsola();
            System.out.println("///-- MENÚ ----/////");
            System.out.println("1. Crear playlist");
            System.out.println("2. Agregar canción");
            System.out.println("3. Mostrar canciones");
            System.out.println("4. Buscar canción por título");
            System.out.println("5. Eliminar canción por título");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opción inválida. Ingrese un número.\n");
                continue;
            }

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el nombre de la playlist: ");
                    String nombre = sc.nextLine();
                    if (nombre.trim().isEmpty()) {
                        System.out.println("Nombre de playlist no puede estar vacío.\n");
                    } else {
                        playlist = new Playlist(nombre);
                        System.out.println("Playlist creada exitosamente.\n");
                    }
                    break;
                case 2:
                    if (playlist == null) {
                        System.out.println("Debe crear una playlist primero.\n");
                        break;
                    }
                    try {
                        System.out.print("Título de la canción: ");
                        String titulo = sc.nextLine();
                        if (titulo.trim().isEmpty()) throw new IllegalArgumentException("Título vacío.");

                        System.out.print("Artista: ");
                        String artista = sc.nextLine();
                        if (artista.trim().isEmpty()) throw new IllegalArgumentException("Artista vacío.");

                        System.out.print("Duración (en segundos): ");
                        int duracion = Integer.parseInt(sc.nextLine());
                        if (duracion <= 0) throw new IllegalArgumentException("Duración inválida.");

                        playlist.agregarCancion(titulo, artista, duracion);
                    } catch (Exception e) {
                        System.out.println("Error al ingresar datos: " + e.getMessage() + "\n");
                    }
                    break;
                case 3:
                    if (playlist != null) playlist.mostrarCanciones();
                    else System.out.println("Debe crear una playlist primero.\n");
                    break;
                case 4:
                    if (playlist != null) {
                        System.out.print("Ingrese el título de la canción a buscar: ");
                        String buscar = sc.nextLine();
                        playlist.buscarCancion(buscar);
                    } else System.out.println("Debe crear una playlist primero.\n");
                    break;
                case 5:
                    if (playlist != null) {
                        System.out.print("Ingrese el título de la canción a eliminar: ");
                        String eliminar = sc.nextLine();
                        playlist.eliminarCancion(eliminar);
                    } else System.out.println("Debe crear una playlist primero.\n");
                    break;
                case 0:
                    guardarDatos(playlist);
                    System.out.println("Saliendo del programa.");
                    break;
                default:
                    System.out.println("Opción inválida.\n");
            }

            if (opcion != 0) {
                System.out.println("Presione Enter para continuar...");
                sc.nextLine();
            }

        } while (opcion != 0);
    }

    public static void limpiarConsola() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (Exception e) {
            System.out.println("No se pudo limpiar la consola.");
        }
    }

    public static void guardarDatos(Playlist playlist) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(playlist);
        } catch (IOException e) {
            System.out.println("Error al guardar los datos.");
        }
    }

    public static Playlist cargarDatos() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            return (Playlist) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}
