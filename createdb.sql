-- MySQL Script generated by MySQL Workbench
-- Fri Dec  1 11:11:46 2017
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema bac
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema bac
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `bac` DEFAULT CHARACTER SET utf8 ;
USE `bac` ;

-- -----------------------------------------------------
-- Table `bac`.`Epreuve`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bac`.`Epreuve` (
  `idEpreuve` VARCHAR(15) NOT NULL,
  `libelle` VARCHAR(45) NULL,
  `session` INT NULL,
  PRIMARY KEY (`idEpreuve`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bac`.`Profil`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bac`.`Profil` (
  `serie` VARCHAR(10) NOT NULL,
  `filliere` VARCHAR(45) NOT NULL,
  `specialite` VARCHAR(45) NOT NULL,
  `section` VARCHAR(10) NOT NULL,
  `annee` INT NOT NULL,
  PRIMARY KEY (`serie`, `filliere`, `specialite`, `section`, `annee`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bac`.`Candidat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bac`.`Candidat` (
  `idCandidat` INT NOT NULL,
  `Profil_serie` VARCHAR(10) NOT NULL,
  `Profil_filliere` VARCHAR(45) NOT NULL,
  `Profil_specialite` VARCHAR(45) NOT NULL,
  `Profil_section` VARCHAR(10) NOT NULL,
  `Profil_annee` INT NOT NULL,
  PRIMARY KEY (`idCandidat`),
  INDEX `fk_Candidat_Profil1_idx` (`Profil_serie` ASC, `Profil_filliere` ASC, `Profil_specialite` ASC, `Profil_section` ASC, `Profil_annee` ASC),
  CONSTRAINT `fk_Candidat_Profil1`
    FOREIGN KEY (`Profil_serie` , `Profil_filliere` , `Profil_specialite` , `Profil_section` , `Profil_annee`)
    REFERENCES `bac`.`Profil` (`serie` , `filliere` , `specialite` , `section` , `annee`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bac`.`Resultat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bac`.`Resultat` (
  `Candidat_idCandidat` INT NOT NULL,
  `Epreuve_idEpreuve` VARCHAR(15) NOT NULL,
  `Matiere_idMatiere` INT NULL,
  `note` FLOAT NULL,
  PRIMARY KEY (`Candidat_idCandidat`, `Epreuve_idEpreuve`),
  INDEX `fk_Candidat_has_Epreuve_Epreuve1_idx` (`Epreuve_idEpreuve` ASC),
  INDEX `fk_Candidat_has_Epreuve_Candidat1_idx` (`Candidat_idCandidat` ASC),
  CONSTRAINT `fk_Candidat_has_Epreuve_Candidat1`
    FOREIGN KEY (`Candidat_idCandidat`)
    REFERENCES `bac`.`Candidat` (`idCandidat`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Candidat_has_Epreuve_Epreuve1`
    FOREIGN KEY (`Epreuve_idEpreuve`)
    REFERENCES `bac`.`Epreuve` (`idEpreuve`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bac`.`Matiere`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bac`.`Matiere` (
  `idMatiere` INT NOT NULL,
  `libelle` VARCHAR(45) NULL,
  PRIMARY KEY (`idMatiere`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bac`.`Determiner`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bac`.`Determiner` (
  `Profil_serie` VARCHAR(10) NOT NULL,
  `Profil_filliere` VARCHAR(45) NOT NULL,
  `Profil_specialite` VARCHAR(45) NOT NULL,
  `Profil_section` VARCHAR(10) NOT NULL,
  `Profil_annee` INT NOT NULL,
  `Epreuve_idEpreuve` VARCHAR(15) NOT NULL,
  `coefficient` INT NULL DEFAULT 1,
  `composition` VARCHAR(15) NULL,
  `bonus` VARCHAR(15) NULL,
  `facultatif` VARCHAR(15) NULL,
  PRIMARY KEY (`Profil_serie`, `Profil_filliere`, `Profil_specialite`, `Profil_section`, `Profil_annee`, `Epreuve_idEpreuve`),
  INDEX `fk_Profil_has_Epreuve_Epreuve1_idx` (`Epreuve_idEpreuve` ASC),
  INDEX `fk_Profil_has_Epreuve_Profil1_idx` (`Profil_serie` ASC, `Profil_filliere` ASC, `Profil_specialite` ASC, `Profil_section` ASC, `Profil_annee` ASC),
  CONSTRAINT `fk_Profil_has_Epreuve_Profil1`
    FOREIGN KEY (`Profil_serie` , `Profil_filliere` , `Profil_specialite` , `Profil_section` , `Profil_annee`)
    REFERENCES `bac`.`Profil` (`serie` , `filliere` , `specialite` , `section` , `annee`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Profil_has_Epreuve_Epreuve1`
    FOREIGN KEY (`Epreuve_idEpreuve`)
    REFERENCES `bac`.`Epreuve` (`idEpreuve`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bac`.`Composer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bac`.`Composer` (
  `Epreuve_idEpreuve1` VARCHAR(15) NOT NULL,
  `Epreuve_idEpreuve2` VARCHAR(15) NOT NULL,
  PRIMARY KEY (`Epreuve_idEpreuve1`, `Epreuve_idEpreuve2`),
  INDEX `fk_Epreuve_has_Epreuve_Epreuve2_idx` (`Epreuve_idEpreuve2` ASC),
  INDEX `fk_Epreuve_has_Epreuve_Epreuve1_idx` (`Epreuve_idEpreuve1` ASC),
  CONSTRAINT `fk_Epreuve_has_Epreuve_Epreuve1`
    FOREIGN KEY (`Epreuve_idEpreuve1`)
    REFERENCES `bac`.`Epreuve` (`idEpreuve`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Epreuve_has_Epreuve_Epreuve2`
    FOREIGN KEY (`Epreuve_idEpreuve2`)
    REFERENCES `bac`.`Epreuve` (`idEpreuve`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bac`.`Preciser`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bac`.`Preciser` (
  `Matiere_idMatiere` INT NOT NULL,
  `Epreuve_idEpreuve` VARCHAR(15) NOT NULL,
  PRIMARY KEY (`Matiere_idMatiere`, `Epreuve_idEpreuve`),
  INDEX `fk_Matiere_has_Epreuve_Epreuve1_idx` (`Epreuve_idEpreuve` ASC),
  INDEX `fk_Matiere_has_Epreuve_Matiere1_idx` (`Matiere_idMatiere` ASC),
  CONSTRAINT `fk_Matiere_has_Epreuve_Matiere1`
    FOREIGN KEY (`Matiere_idMatiere`)
    REFERENCES `bac`.`Matiere` (`idMatiere`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Matiere_has_Epreuve_Epreuve1`
    FOREIGN KEY (`Epreuve_idEpreuve`)
    REFERENCES `bac`.`Epreuve` (`idEpreuve`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bac`.`Remplacer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bac`.`Remplacer` (
  `Epreuve_idEpreuve1` VARCHAR(15) NOT NULL,
  `Epreuve_idEpreuve2` VARCHAR(15) NOT NULL,
  PRIMARY KEY (`Epreuve_idEpreuve1`, `Epreuve_idEpreuve2`),
  INDEX `fk_Epreuve_has_Epreuve_Epreuve4_idx` (`Epreuve_idEpreuve2` ASC),
  INDEX `fk_Epreuve_has_Epreuve_Epreuve3_idx` (`Epreuve_idEpreuve1` ASC),
  CONSTRAINT `fk_Epreuve_has_Epreuve_Epreuve3`
    FOREIGN KEY (`Epreuve_idEpreuve1`)
    REFERENCES `bac`.`Epreuve` (`idEpreuve`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Epreuve_has_Epreuve_Epreuve4`
    FOREIGN KEY (`Epreuve_idEpreuve2`)
    REFERENCES `bac`.`Epreuve` (`idEpreuve`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;