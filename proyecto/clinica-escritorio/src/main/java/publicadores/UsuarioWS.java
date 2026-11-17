package publicadores;

/** DTO de usuario devuelto por el servicio SOAP. */
public class UsuarioWS {
    private String email;
    private String nombre;
    private TipoUsuario tipo;
    private String token;

    public UsuarioWS() {}
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
