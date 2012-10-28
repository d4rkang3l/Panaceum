package com.unlimited.panaceum.ir

import japa.parser.ast.body._
import com.unlimited.panaceum.ir.TypeConversions._
import scala.collection.JavaConversions._

/**
 * Body Declaration enhanced with XML representation.
 *
 * @author Iulian Dumitru 
 */
class RichBodyDeclaration(val bd: BodyDeclaration) extends XMLRepresentation {

  implicit def anyToText(a: AnyVal) = xml.Text(a.toString)

  def toXML: String = bd match {

    case method: MethodDeclaration => {

      val modifiers = method.getModifiers
      val isFinal = ModifierSet.isFinal(modifiers)
      val isAbstract = ModifierSet.isAbstract(modifiers)

      val modifierName = if (ModifierSet.isPrivate(modifiers)) "private"
      else if (ModifierSet.isPublic(modifiers)) "public"
      else if (ModifierSet.isProtected(modifiers)) "protected"
      else throw new Error("Cannot find modifier name!")

      val params = method.getParameters


      val body = method.getBody
      val stmts = body.getStmts


      //generate <method> tags
      params match {
        case null => {
          <method name={method.getName} modifer={modifierName} isFinal={isFinal} isAbstract={isAbstract}>
            <body>
              {for (stmt <- body.getStmts) yield <stmt type={stmt.getClass.getSimpleName}/>}
            </body>
          </method>.toString()
        }
        case _ =>
          <method name={method.getName} modifer={modifierName} isFinal={isFinal} isAbstract={isAbstract}>
            <parameters>
              {for (param <- params) yield <parameter name={param.getId.getName} type={param.getType.typeName}/>}
            </parameters>
            <body>
              {for (stmt <- body.getStmts) yield <stmt type={stmt.getClass.getSimpleName}/>}
            </body>
          </method>.toString()
      }

    }

    case field: FieldDeclaration => {

      val modifiers = field.getModifiers
      val fieldTypeName = field.getType.typeName
      val isFinal = ModifierSet.isFinal(modifiers)
      val variables = field.getVariables

      variables.collect {
        case v => s"""<field name="${v.getId}" type="${fieldTypeName}" isFinal="${isFinal}" />"""
      }.mkString("")

    }

    case constructor: ConstructorDeclaration => {

      val modifiers = constructor.getModifiers
      val modifierName = if (ModifierSet.isPrivate(modifiers)) "private"
      else if (ModifierSet.isPublic(modifiers)) "public"
      else if (ModifierSet.isProtected(modifiers)) "protected"
      else throw new Error("Cannot find modifier name!")

      val parameters = constructor.getParameters
      val typeParameters = constructor.getTypeParameters

      val block = constructor.getBlock
      val stmts = block.getStmts


      <constructor modifier={modifierName}>
        <parameters>
          {for (p <- parameters) yield <parameter name={p.getId.getName} type={p.getType.typeName}/>}
        </parameters>
      </constructor>.toString()


    }

    case declaration@_ => {
      throw new Error(s"Unknown BodyDeclaration! Found ${declaration}")
    }

  }


}
