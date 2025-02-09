package ru.practicum.android.diploma.vacancy.data.converter

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.util.Converter.convertSalaryToString
import ru.practicum.android.diploma.favorites.domain.entity.VacancyFavorite
import ru.practicum.android.diploma.search.domain.model.Salary
import ru.practicum.android.diploma.vacancy.data.dto.EmployerDto
import ru.practicum.android.diploma.vacancy.data.dto.ExperienceDto
import ru.practicum.android.diploma.vacancy.data.dto.KeySkillDto
import ru.practicum.android.diploma.vacancy.data.network.VacancyDetailsResponse
import ru.practicum.android.diploma.vacancy.domain.entity.Vacancy

class VacancyConverter(private val context: Context) {
    fun map(response: VacancyDetailsResponse): Vacancy {
        return Vacancy(
            id = response.id,
            name = response.name,
            salary = getSalary(response.salary),
            companyLogo = getLogo(response.employer),
            companyName = getCompanyName(response.employer),
            area = response.area?.name,
            address = getAddress(response),
            experience = getExperience(response.experience),
            schedule = response.schedule?.name,
            employment = response.employment?.name,
            description = response.description,
            keySkills = getKeySkills(response.keySkills),
            vacancyUrl = response.alternateUrl
        )
    }

    fun mapDetailsToFavorite(response: VacancyDetailsResponse): VacancyFavorite {
        return VacancyFavorite(
            id = response.id,
            name = response.name,
            salary = response.salary,
            employer = response.employer,
            area = response.area,
            address = response.address,
            experience = response.experience,
            schedule = response.schedule,
            employment = response.employment,
            description = response.description,
            keySkills = response.keySkills,
            vacancyUrl = response.alternateUrl
        )
    }

    fun mapFavoriteToDetails(response: VacancyFavorite): VacancyDetailsResponse {
        return VacancyDetailsResponse(
            id = response.id,
            name = response.name,
            salary = response.salary,
            employer = response.employer,
            area = response.area,
            address = response.address,
            experience = response.experience,
            schedule = response.schedule,
            employment = response.employment,
            description = response.description,
            keySkills = response.keySkills,
            alternateUrl = response.vacancyUrl
        )
    }

    fun mapFavoriteToVacancy(response: VacancyFavorite): Vacancy {
        return Vacancy(
            id = response.id,
            name = response.name,
            salary = getSalary(response.salary),
            companyLogo = getLogo(response.employer),
            companyName = getCompanyName(response.employer),
            area = response.area?.name,
            address = getAddressFavorite(response),
            experience = getExperience(response.experience),
            schedule = response.schedule?.name,
            employment = response.employment?.name,
            description = response.description,
            keySkills = getKeySkills(response.keySkills),
            vacancyUrl = response.vacancyUrl
        )
    }

    private fun getKeySkills(keySkills: Any?): String {
        val gson = Gson()
        val keySkillsString = StringBuilder()
        val bulletDot = context.getString(R.string.bullet_dot)

        // Проверяем, является ли keySkills списком и не пусто ли оно
        if (keySkills is List<*>) {
            val type = object : TypeToken<List<KeySkillDto>>() {}.type

            val keySkillList: List<KeySkillDto> = try {
                gson.fromJson(gson.toJson(keySkills), type)
            } catch (e: Exception) {
                emptyList() // В случае ошибки десериализации
            }

            keySkillList.forEach { skill ->
                keySkillsString.append("$bulletDot ${skill.name} <br/>")
            }
        }

        return keySkillsString.toString()
    }

    private fun getExperience(experience: ExperienceDto?): String? {
        return experience?.name
    }

    private fun getLogo(employer: EmployerDto?): String? {
        return employer?.logoUrls?.iconBig
    }

    private fun getAddress(response: VacancyDetailsResponse): String? {
        return if (response.address?.city == null) {
            response.area?.name
        } else {
            response.address.city
        }
    }
    private fun getAddressFavorite(response: VacancyFavorite): String? {
        return if (response.address?.city == null) {
            response.area?.name
        } else {
            response.address.city
        }
    }

    private fun getSalary(salaryDto: Salary?): String {
        val salaryFormatter = convertSalaryToString(salaryDto?.from, salaryDto?.to, salaryDto?.currency)
        return salaryFormatter
    }

    private fun getCompanyName(employer: EmployerDto?): String {
        return employer?.name ?: ""
    }
}
