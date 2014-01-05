package br.com.eltonsantos.financeiro.model;

import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Version;
import java.lang.Override;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import br.com.eltonsantos.financeiro.model.TipoConta;
import javax.persistence.ManyToOne;

@Entity
public class Conta implements Serializable
{

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "id", updatable = false, nullable = false)
   private Long id = null;

   @Version
   @Column(name = "version")
   private int version = 0;

   @Column(length = 75, nullable = false)
   private String nome;

   @Temporal(TemporalType.DATE)
   @Column(nullable = false)
   private Date dataAbertura;

   @Column(nullable = false)
   private BigDecimal saldoInicial;

   @ManyToOne(optional = false)
   private TipoConta tipoConta;

   @Column
   private String descricaoConta;

   public Long getId()
   {
      return this.id;
   }

   public void setId(final Long id)
   {
      this.id = id;
   }

   public int getVersion()
   {
      return this.version;
   }

   public void setVersion(final int version)
   {
      this.version = version;
   }

   @Override
   public boolean equals(Object that)
   {
      if (this == that)
      {
         return true;
      }
      if (that == null)
      {
         return false;
      }
      if (getClass() != that.getClass())
      {
         return false;
      }
      if (id != null)
      {
         return id.equals(((Conta) that).id);
      }
      return super.equals(that);
   }

   @Override
   public int hashCode()
   {
      if (id != null)
      {
         return id.hashCode();
      }
      return super.hashCode();
   }

   public String getNome()
   {
      return this.nome;
   }

   public void setNome(final String nome)
   {
      this.nome = nome;
   }

   public Date getDataAbertura()
   {
      return this.dataAbertura;
   }

   public void setDataAbertura(final Date dataAbertura)
   {
      this.dataAbertura = dataAbertura;
   }

   public BigDecimal getSaldoInicial()
   {
      return this.saldoInicial;
   }

   public void setSaldoInicial(final BigDecimal saldoInicial)
   {
      this.saldoInicial = saldoInicial;
   }

   public TipoConta getTipoConta()
   {
      return this.tipoConta;
   }

   public void setTipoConta(final TipoConta tipoConta)
   {
      this.tipoConta = tipoConta;
   }

   public String getDescricaoConta()
   {
      return this.descricaoConta;
   }

   public void setDescricaoConta(final String descricaoConta)
   {
      this.descricaoConta = descricaoConta;
   }

   @Override
   public String toString()
   {
      String result = getClass().getSimpleName() + " ";
      if (nome != null && !nome.trim().isEmpty())
         result += "nome: " + nome;
      if (descricaoConta != null && !descricaoConta.trim().isEmpty())
         result += ", descricaoConta: " + descricaoConta;
      return result;
   }
}