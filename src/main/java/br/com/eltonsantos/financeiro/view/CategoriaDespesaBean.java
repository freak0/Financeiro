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

import br.com.eltonsantos.financeiro.model.CategoriaDespesa;

/**
 * Backing bean for CategoriaDespesa entities.
 * <p>
 * This class provides CRUD functionality for all CategoriaDespesa entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class CategoriaDespesaBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving CategoriaDespesa entities
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

   private CategoriaDespesa categoriaDespesa;

   public CategoriaDespesa getCategoriaDespesa()
   {
      return this.categoriaDespesa;
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
         this.categoriaDespesa = this.example;
      }
      else
      {
         this.categoriaDespesa = findById(getId());
      }
   }

   public CategoriaDespesa findById(Long id)
   {

      return this.entityManager.find(CategoriaDespesa.class, id);
   }

   /*
    * Support updating and deleting CategoriaDespesa entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.categoriaDespesa);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.categoriaDespesa);
            return "view?faces-redirect=true&id=" + this.categoriaDespesa.getId();
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
         CategoriaDespesa deletableEntity = findById(getId());

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
    * Support searching CategoriaDespesa entities with pagination
    */

   private int page;
   private long count;
   private List<CategoriaDespesa> pageItems;

   private CategoriaDespesa example = new CategoriaDespesa();

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

   public CategoriaDespesa getExample()
   {
      return this.example;
   }

   public void setExample(CategoriaDespesa example)
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
      Root<CategoriaDespesa> root = countCriteria.from(CategoriaDespesa.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<CategoriaDespesa> criteria = builder.createQuery(CategoriaDespesa.class);
      root = criteria.from(CategoriaDespesa.class);
      TypedQuery<CategoriaDespesa> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<CategoriaDespesa> root)
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

   public List<CategoriaDespesa> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back CategoriaDespesa entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<CategoriaDespesa> getAll()
   {

      CriteriaQuery<CategoriaDespesa> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(CategoriaDespesa.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(CategoriaDespesa.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final CategoriaDespesaBean ejbProxy = this.sessionContext.getBusinessObject(CategoriaDespesaBean.class);

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

            return String.valueOf(((CategoriaDespesa) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private CategoriaDespesa add = new CategoriaDespesa();

   public CategoriaDespesa getAdd()
   {
      return this.add;
   }

   public CategoriaDespesa getAdded()
   {
      CategoriaDespesa added = this.add;
      this.add = new CategoriaDespesa();
      return added;
   }
}