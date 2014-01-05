package br.com.eltonsantos.financeiro.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.eltonsantos.financeiro.model.Conta;
import br.com.eltonsantos.financeiro.model.TipoConta;

/**
 * Backing bean for Conta entities.
 * <p>
 * This class provides CRUD functionality for all Conta entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class ContaBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Conta entities
    */

   private Long id;

   public Long getId()
   {
      return this.id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   private Conta conta;

   public Conta getConta()
   {
      return this.conta;
   }

   @Inject
   private Conversation conversation;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   public String create()
   {

      this.conversation.begin();
      return "create?faces-redirect=true";
   }

   public void retrieve()
   {

      if (FacesContext.getCurrentInstance().isPostback())
      {
         return;
      }

      if (this.conversation.isTransient())
      {
         this.conversation.begin();
      }

      if (this.id == null)
      {
         this.conta = this.example;
      }
      else
      {
         this.conta = findById(getId());
      }
   }

   public Conta findById(Long id)
   {

      return this.entityManager.find(Conta.class, id);
   }

   /*
    * Support updating and deleting Conta entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.conta);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.conta);
            return "view?faces-redirect=true&id=" + this.conta.getId();
         }
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   public String delete()
   {
      this.conversation.end();

      try
      {
         Conta deletableEntity = findById(getId());

         this.entityManager.remove(deletableEntity);
         this.entityManager.flush();
         return "search?faces-redirect=true";
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   /*
    * Support searching Conta entities with pagination
    */

   private int page;
   private long count;
   private List<Conta> pageItems;

   private Conta example = new Conta();

   public int getPage()
   {
      return this.page;
   }

   public void setPage(int page)
   {
      this.page = page;
   }

   public int getPageSize()
   {
      return 10;
   }

   public Conta getExample()
   {
      return this.example;
   }

   public void setExample(Conta example)
   {
      this.example = example;
   }

   public void search()
   {
      this.page = 0;
   }

   public void paginate()
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

      // Populate this.count

      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      Root<Conta> root = countCriteria.from(Conta.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Conta> criteria = builder.createQuery(Conta.class);
      root = criteria.from(Conta.class);
      TypedQuery<Conta> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Conta> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String nome = this.example.getNome();
      if (nome != null && !"".equals(nome))
      {
         predicatesList.add(builder.like(root.<String> get("nome"), '%' + nome + '%'));
      }
      TipoConta tipoConta = this.example.getTipoConta();
      if (tipoConta != null)
      {
         predicatesList.add(builder.equal(root.get("tipoConta"), tipoConta));
      }
      String descricaoConta = this.example.getDescricaoConta();
      if (descricaoConta != null && !"".equals(descricaoConta))
      {
         predicatesList.add(builder.like(root.<String> get("descricaoConta"), '%' + descricaoConta + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Conta> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Conta entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Conta> getAll()
   {

      CriteriaQuery<Conta> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(Conta.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(Conta.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final ContaBean ejbProxy = this.sessionContext.getBusinessObject(ContaBean.class);

      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context,
               UIComponent component, String value)
         {

            return ejbProxy.findById(Long.valueOf(value));
         }

         @Override
         public String getAsString(FacesContext context,
               UIComponent component, Object value)
         {

            if (value == null)
            {
               return "";
            }

            return String.valueOf(((Conta) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Conta add = new Conta();

   public Conta getAdd()
   {
      return this.add;
   }

   public Conta getAdded()
   {
      Conta added = this.add;
      this.add = new Conta();
      return added;
   }
}