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

import br.com.eltonsantos.financeiro.model.TipoConta;

/**
 * Backing bean for TipoConta entities.
 * <p>
 * This class provides CRUD functionality for all TipoConta entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class TipoContaBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving TipoConta entities
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

   private TipoConta tipoConta;

   public TipoConta getTipoConta()
   {
      return this.tipoConta;
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
         this.tipoConta = this.example;
      }
      else
      {
         this.tipoConta = findById(getId());
      }
   }

   public TipoConta findById(Long id)
   {

      return this.entityManager.find(TipoConta.class, id);
   }

   /*
    * Support updating and deleting TipoConta entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.tipoConta);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.tipoConta);
            return "view?faces-redirect=true&id=" + this.tipoConta.getId();
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
         TipoConta deletableEntity = findById(getId());

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
    * Support searching TipoConta entities with pagination
    */

   private int page;
   private long count;
   private List<TipoConta> pageItems;

   private TipoConta example = new TipoConta();

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

   public TipoConta getExample()
   {
      return this.example;
   }

   public void setExample(TipoConta example)
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
      Root<TipoConta> root = countCriteria.from(TipoConta.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<TipoConta> criteria = builder.createQuery(TipoConta.class);
      root = criteria.from(TipoConta.class);
      TypedQuery<TipoConta> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<TipoConta> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String descricao = this.example.getDescricao();
      if (descricao != null && !"".equals(descricao))
      {
         predicatesList.add(builder.like(root.<String> get("descricao"), '%' + descricao + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<TipoConta> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back TipoConta entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<TipoConta> getAll()
   {

      CriteriaQuery<TipoConta> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(TipoConta.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(TipoConta.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final TipoContaBean ejbProxy = this.sessionContext.getBusinessObject(TipoContaBean.class);

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

            return String.valueOf(((TipoConta) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private TipoConta add = new TipoConta();

   public TipoConta getAdd()
   {
      return this.add;
   }

   public TipoConta getAdded()
   {
      TipoConta added = this.add;
      this.add = new TipoConta();
      return added;
   }
}