package br.com.projeto.sistema.security.enums;

public enum RoleEnum {
	ROLE_USER_A("A", 1),
	ROLE_USER_B("B", 2);

	private String tipo;
    private int codigo;
    
    private RoleEnum(String tipo, int codigo) {
        this.tipo = tipo;
        this.codigo = codigo;
    }

    private RoleEnum(int codigo) {
        this.codigo = codigo;
    }
    public int getCodigo () {
        return codigo;
    }
    public String getTipo () {
        return tipo;
    }
}