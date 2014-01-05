package br.com.eltonsantos.financeiro.model;

import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Version;
import java.lang.Override;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import br.com.eltonsantos.financeiro.model.CategoriaDespesa;
import javax.persistence.ManyToOne;

@Entity
public class ContaPagar implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id = null;

	@Version
	@Column(name = "version")
	private int version = 0;

	@Column(length = 75, nullable = false)
	private String nome;

	@Column(nullable = false)
	private BigDecimal valor;

	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	private Date dataVencimento;

	@Column(length = 50)
	private String codigoDeBarra;

	@Column(nullable = false)
	private boolean estaPaga = false;

	@ManyToOne(optional = false)
	private CategoriaDespesa categoriaDespesa;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		if (that == null) {
			return false;
		}
		if (getClass() != that.getClass()) {
			return false;
		}
		if (id != null) {
			return id.equals(((ContaPagar) that).id);
		}
		return super.equals(that);
	}

	@Override
	public int hashCode() {
		if (id != null) {
			return id.hashCode();
		}
		return super.hashCode();
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(final String nome) {
		this.nome = nome;
	}

	public BigDecimal getValor() {
		return this.valor;
	}

	public void setValor(final BigDecimal valor) {
		this.valor = valor;
	}

	public Date getDataVencimento() {
		return this.dataVencimento;
	}

	public void setDataVencimento(final Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public String getCodigoDeBarra() {
		return this.codigoDeBarra;
	}

	public void setCodigoDeBarra(final String codigoDeBarra) {
		this.codigoDeBarra = codigoDeBarra;
	}

	public boolean getEstaPaga() {
		return this.estaPaga;
	}

	public void setEstaPaga(final boolean estaPaga) {
		this.estaPaga = estaPaga;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (nome != null && !nome.trim().isEmpty())
			result += "nome: " + nome;
		result += ", estaPaga: " + estaPaga;
		return result;
	}

	public CategoriaDespesa getCategoriaDespesa() {
		return this.categoriaDespesa;
	}

	public void setCategoriaDespesa(final CategoriaDespesa categoriaDespesa) {
		this.categoriaDespesa = categoriaDespesa;
	}
}